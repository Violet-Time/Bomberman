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

public class Pawn extends DynamicGameObject {


    protected boolean alive;

    /** Current direction */
    protected Move direction;

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

    @JsonIgnore
    public final static String TYPE = "Pawn";

    @JsonIgnore
    protected static final Size size = new Size(48, 48);

    @JsonIgnore
    private final Logger log = LoggerFactory.getLogger(Pawn.class);


    public Pawn(Action action, GameEntityRepository gameEntityRepository) {
        super(gameEntityRepository, size, TYPE);
        this.alive = true;
        this.action = action;

        getCollision().setSize(new Size(20, 20));
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void update() {
    }

    public void plantBomb() {

        if (!gameEntityRepository.getBombs(getEntityPosition()).isEmpty()) {
            return;
        }

        if (this.bombs.size() < bombsMax) {
            Bomb bomb = new Bomb(gameEntityRepository, bombStrength);
            bomb.setEntityPosition(getEntityPosition());

            bombs.add(bomb);
            gameEntityRepository.addBomb(bomb);

            setEscapeBomb(bomb);

            bomb.setExplodeListener((e) -> {
                this.bombs.remove(bomb);
                return e;
            });

            log.debug("Plant bomb \n" + bomb);
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

        int edgeSize = 26;

        Vector2 bitmapPosition = collision.getBitmapPosition().sub(6,6);

        // possible fix position we are going to choose from
        Vector2 pos1 = getEntityPosition().add(dir.getY(), dir.getX());

        Vector2 bmp1 = convertToBitmapPosition(pos1);

        Vector2 pos2 = getEntityPosition().sub(dir.getY(), dir.getX());

        Vector2 bmp2 = convertToBitmapPosition(pos2);

        // in front of current position
        if (gameEntityRepository.getTileMaterial(getEntityPosition().add(dir)) == Material.GRASS) {

            Vector2 vector2 = convertToBitmapPosition(collision.getEntityPosition());

            if (vector2.sub(bitmapPosition.add(dir.getY(), dir.getX())).magnitude() >
                    vector2.sub(bitmapPosition.sub(dir.getY(), dir.getX())).magnitude()) {
                return new Vector2(-dir.getY(), -dir.getX());
            } else {
                return new Vector2(dir.getY(), dir.getX());
            }
        }

        // right bottom
        // left top
        if (gameEntityRepository.getTileMaterial(pos1) == Material.GRASS &&
                gameEntityRepository.getTileMaterial(pos1.add(dir)) == Material.GRASS &&
                    bitmapPosition.sub(bmp1).magnitude() <= edgeSize) {
            return new Vector2(dir.getY(), dir.getX());
        }

        // right top
        // left bottom
        if (gameEntityRepository.getTileMaterial(pos2) == Material.GRASS &&
                gameEntityRepository.getTileMaterial(pos2.add(dir)) == Material.GRASS &&
                    bitmapPosition.sub(bmp2).magnitude() <= edgeSize) {
            return new Vector2(-dir.getY(), -dir.getX());
        }

        return null;
    }

    /**
     * Returns true when collision is detected and we should not move to target position.
     */
    public boolean detectWallCollision(Rect rect) {

        for (int i = 0; i < 8; i++) {

            Vector2 dir = rect.getEntityPosition().add(star(i));
            Tile tile = gameEntityRepository.getTile(dir);

            if (tile != null && !(tile instanceof Grass)) {

                if (tile.isColliding(rect)) {
                    log.debug("Detect Wall \n" + tile + "\n" + this + "\n" + rect);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns true when the bomb collision is detected and we should not move to target position.
     */
    public boolean detectBombCollision(Rect rect) {

        // Get a bomb in position
        Optional<Bomb> bomb = gameEntityRepository.getBombs(rect.getEntityPosition()).stream().findFirst();

        if (bomb.isPresent()) {
            log.debug("Detect bomb \n" + bomb.get() + "\n" + this);
            // Allow to escape from bomb that appeared on my field
            return bomb.get() != this.escapeBomb;
        }

        // I have escaped already
        if (this.escapeBomb != null) {
            this.escapeBomb = null;
        }

        return false;
    }

    public boolean detectFireCollision() {
        for (int i = 0; i < 8; i++) {
            Vector2 dir = getEntityPosition().add(star(i));
            Optional<Fire> fire = gameEntityRepository.getFires(dir).stream().filter(e -> e.getBomb().isExploded()).findFirst();
            if (fire.isPresent() &&
                isColliding(fire.get())) {
                log.debug("Detect fire \n" + fire + "\n" + this);
                return true;
            }
        }
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
                log.debug("Apply bonus \n" + bonus + "\n" + this);
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
        log.debug("Die pawn" + this);

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
