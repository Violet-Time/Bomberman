package com.example.bomberman.model.game.dynamic.pawn;

import com.example.bomberman.model.Action;
import com.example.bomberman.model.Move;
import com.example.bomberman.model.game.*;
import com.example.bomberman.model.game.dynamic.Bomb;
import com.example.bomberman.model.game.dynamic.DynamicGameObject;
import com.example.bomberman.model.game.dynamic.Fire;
import com.example.bomberman.model.game.staticObj.bonus.Bonus;
import com.example.bomberman.model.game.staticObj.bonus.BonusType;
import com.example.bomberman.model.game.staticObj.tile.Grass;
import com.example.bomberman.model.game.staticObj.tile.Material;
import com.example.bomberman.model.game.staticObj.tile.Tile;
import com.example.bomberman.repos.GameEntityRepository;
import com.example.bomberman.util.Utils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@JsonIgnoreProperties(value = {"log", "TYPE", "size", "velocity", "bombsMax", "bombStrength", "bombs",
                                "escapeBomb", "deadTimer", "action"})
public class Pawn extends DynamicGameObject {

    @JsonIgnore
    private Logger log = LoggerFactory.getLogger(Pawn.class);
    @JsonIgnore
    public final static String TYPE = "Pawn";

    @JsonIgnore
    protected static final Size size = new Size(48, 48);

    /**
     * Moving speed
     */
    @JsonIgnore
    protected int velocity = 2;

    /**
     * Max number of bombs user can spawn
     */
    @JsonIgnore
    protected int bombsMax = 1;

    /**
     * How far the fire reaches when bomb explodes
     */
    @JsonIgnore
    protected int bombStrength = 1;
    protected boolean alive;

    protected Move direction;
    @JsonIgnore
    protected List<Bomb> bombs = new ArrayList<>();

    /**
     * Bomb that player can escape from even when there is a collision
     */
    @JsonIgnore
    protected Bomb escapeBomb = null;

    @JsonIgnore
    protected int deadTimer = 0;

    @JsonIgnore
    Action action;

    //private Point pixels;

    //GameEngine gameEngine;

    public Pawn(Action action, GameEntityRepository gameEntityRepository) {
        super(gameEntityRepository, size, TYPE);
        this.alive = true;
        this.action = action;

        getCollision().setSize(new Size(20, 20));
        //this.pixels = Utils.convertToBitmapPosition(position);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void update() {
    }

    public void plantBomb() {
        int unexplodedBombs = 0;

        if (!gameEntityRepository.getBombs(getEntityPosition()).isEmpty()) {
            return;
        }

        for (Bomb bomb : this.bombs) {
            if (!bomb.isExploded()) {
                unexplodedBombs++;
            }
        }

        if (unexplodedBombs < bombsMax) {
            Bomb bomb = new Bomb(gameEntityRepository, bombStrength);
            bomb.setEntityPosition(getEntityPosition());

            bombs.add(bomb);
            gameEntityRepository.addBomb(bomb);

            setEscapeBomb(bomb);

            bomb.setExplodeListener((e) -> {
                this.bombs.remove(bomb);
                return e;
            });
        }
    }



    public void setDirection(Move direction) {
        this.direction = direction;
    }

    public void resetDirection() {
        this.direction = null;
    }

    /**
     * Checks whether we are on corner to target position.
     * Returns position where we should move before we can go to target.
     */
    public Vector2 getCornerFix(Vector2 dir) {
        int edgeSize = 30;

        // fix position to where we should go first
        Vector2 entityPosition = null;
        Vector2 bitmapPosition = getBitmapPosition();

        // possible fix position we are going to choose from
        Vector2 pos1 = getEntityPosition().add(dir.getY(), dir.getX());

        Vector2 bmp1 = convertToBitmapPosition(pos1);

        Vector2 pos2 = getEntityPosition().sub(dir.getY(), dir.getX());

        Vector2 bmp2 = convertToBitmapPosition(pos2);

        // in front of current position
        if (gameEntityRepository.getTileMaterial(getEntityPosition().add(dir)) == Material.GRASS) {
            return dir;
            //entityPosition = getEntityPosition();
        }
        // right bottom
        // left top
        if (Math.sqrt(Math.pow(bitmapPosition.getX() - bmp1.getX(), 2) + Math.pow(bitmapPosition.getY() - bmp1.getY(), 2)) <=
                Math.sqrt(Math.pow(bitmapPosition.getX() - bmp2.getX(), 2) + Math.pow(bitmapPosition.getY() - bmp2.getY(), 2))
        ) {
            if (gameEntityRepository.getTileMaterial(pos1) == Material.GRASS &&
                    gameEntityRepository.getTileMaterial(pos1.add(dir)) == Material.GRASS) {
                return new Vector2(dir.getY(), dir.getX());
            } else if (gameEntityRepository.getTileMaterial(pos2) == Material.GRASS
                    && gameEntityRepository.getTileMaterial(pos2.add(dir)) == Material.GRASS) {
                return new Vector2(-dir.getY(), -dir.getX());
            }
            //entityPosition = pos1;
        } else {
            if (gameEntityRepository.getTileMaterial(pos2) == Material.GRASS
                    && gameEntityRepository.getTileMaterial(pos2.add(dir)) == Material.GRASS) {
                return new Vector2(-dir.getY(), -dir.getX());
            } else if (gameEntityRepository.getTileMaterial(pos1) == Material.GRASS &&
                    gameEntityRepository.getTileMaterial(pos1.add(dir)) == Material.GRASS) {
                return new Vector2(dir.getY(), dir.getX());
            }
        }

        // right top
        // left bottom
        /*else if (gameEntityRepository.getTileMaterial(pos2) == Material.GRASS
                && gameEntityRepository.getTileMaterial(pos2.add(dir)) == Material.GRASS) {
            return new Vector2(-dir.getY(), -dir.getX());
            //entityPosition = pos2;
        }*/

        /*if (gameEntityRepository.getTileMaterial(pos1) == Material.GRASS &&
                gameEntityRepository.getTileMaterial(pos1.add(dir)) == Material.GRASS) {
            return new Vector2(dir.getY(), dir.getX());
        }*/

        /*if (entityPosition != null && gameEntityRepository.getTileMaterial(entityPosition) == Material.GRASS) {
            return convertToBitmapPosition(entityPosition);
        }*/

        return null;
    }

    /**
     * Calculates and updates entity position according to its actual bitmap position
     */
    /*public void updatePosition() {
        this.position = Utils.convertToEntityPosition(this.pixels);
    }*/

    /**
     * Returns true when collision is detected and we should not move to target position.
     */
    public boolean detectWallCollision(Vector2 vector) {
        Rect rect = new Rect(this.collision);
        rect.setBitmapPosition(rect.getBitmapPosition().add(vector));

        for (int i = 0; i < 8; i++) {
            Vector2 dir = rect.getEntityPosition().add(star(i));
            Tile tile = gameEntityRepository.getTile(dir);
            //log.info(getBitmapPosition().toString());
            //log.info(tile.getBitmapPosition().toString());
            //log.info(dir.toString());
            if (tile != null && !(tile instanceof Grass)) {
                /*if (this instanceof Player) {
                    log.info(rect.getBitmapPosition().toString());
                    log.info(tile.getBitmapPosition().toString());
                }*/
                if (tile.isColliding(rect)) {

                    //log.info(getBitmapPosition().toString());
                    //log.info(tile.getBitmapPosition().toString());
                    return true;
                }
            }
        }
        /*Rect player = new Rect();
        player.setLeft(position.getX());
        player.setBottom(position.getY());
        player.setRight(player.getLeft() + this.size.getWight());
        player.setTop(player.getBottom() + this.size.getHeight());

        // Check possible collision with all wall and wood tiles
        List<Tile> tiles = gameEntityRepository.getAllTitles();
        for (Tile e : tiles) {
            Point tilePosition = e.getBitmapPosition();

            Rect tile = new Rect();
            tile.setLeft(tilePosition.getX() * GameEngine.tileSize + 2);
            tile.setBottom(tilePosition.getY() * GameEngine.tileSize + 2);
            tile.setRight(tile.getLeft() + GameEngine.tileSize - 4);
            tile.setTop(tile.getBottom() + GameEngine.tileSize - 4);

            if (GameEngine.intersectRect(player, tile)) {
                return true;
            }
        }*/
        return false;
    }

    /**
     * Returns true when the bomb collision is detected and we should not move to target position.
     */
    public boolean detectBombCollision(Vector2 pixels) {
        Vector2 position = Utils.convertToEntityPosition(pixels);

        Optional<Bomb> bomb = gameEntityRepository.getBombs(position).stream().findFirst();

        if (bomb.isPresent()) {
            return bomb.get() != this.escapeBomb;
        }

        /*for (Bomb bomb : gameEntityRepository.getAllBombs()) {
            // Compare bomb position
            if (bomb.getRect().comparePositions(position)) {
                // Allow to escape from bomb that appeared on my field
                if (bomb == this.escapeBomb) {
                    return false;
                } else {
                    return true;
                }
            }
        }*/

        // I have escaped already
        if (this.escapeBomb != null) {
            this.escapeBomb = null;
        }

        return false;
    }

    public boolean detectFireCollision() {
        for (int i = 0; i < 8; i++) {
            Vector2 dir = getEntityPosition().add(star(i));
            Optional<Fire> fire = gameEntityRepository.getFires(dir).stream().findFirst();
            if (fire.isPresent() &&
                isColliding(fire.get())) {
                return true;
            }
        }
        /*for (Bomb bomb : gameEntityRepository.getAllBombs()) {
            for (Fire fire : bomb.getFires()) {
                boolean collision = bomb.isExploded() && rect.comparePositions(fire.getRect());
                if (collision) {
                    return true;
                }
            }
        }*/
        return false;
    }

    /**
     * Checks whether we have got bonus and applies it.
     */
    public void handleBonusCollision() {
        for (int i = 0; i < 8; i++) {
            Vector2 dir = getEntityPosition().add(star(i));
            Optional<Bonus> bonus = gameEntityRepository.getBonuses(dir).stream().findFirst();
            if (bonus.isPresent() &&
                    isColliding(bonus.get())) {
                applyBonus(bonus.get());
                bonus.get().destroy();
            }
        }
    }

    /**
     * Applies bonus.
     */
    public void applyBonus(Bonus bonus) {
        if (bonus.getBonusType() == BonusType.SPEED) {
            this.velocity += 0.8;
        } else if (bonus.getBonusType() == BonusType.BOMBS) {
            this.bombsMax++;
        } else if (bonus.getBonusType() == BonusType.RANGE) {
            this.bombStrength++;
        }
    }

    public void die() {
        this.alive = false;

        /*if (gameObjectRepository.countPlayersAlive() == 1 && gameObjectRepository.playersCount() == 2) {
            gameEngine.gameOver('win');
        } else if (gameObjectRepository.countPlayersAlive() == 0) {
            gameEngine.gameOver('lose');
        }*/

    }



    public void setEscapeBomb(Bomb escapeBomb) {
        this.escapeBomb = escapeBomb;
    }

    public boolean isAlive() {
        return alive;
    }

    @Override
    public String toString() {
        return "Pawn{" +
                super.toString() +
                '}';
    }

    @Override
    public Vector2 getEntityPosition() {
        return collision.getEntityPosition();
    }

    @Override
    public void setBitmapPosition(Vector2 bitmapPosition) {
        super.setBitmapPosition(bitmapPosition);
        collision.setBitmapPosition(getBitmapPosition().add(5, 3));
    }

    @Override
    public void setEntityPosition(Vector2 entityPosition) {
        super.setEntityPosition(entityPosition);
        collision.setBitmapPosition(getBitmapPosition().add(5, 3));
    }
}
