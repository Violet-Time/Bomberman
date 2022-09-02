package com.example.bomberman.service.tick.gameMechanics.dynamic.pawn;

import com.example.bomberman.model.Action;
import com.example.bomberman.model.Move;
import com.example.bomberman.service.tick.gameMechanics.Vector2;
import com.example.bomberman.service.tick.gameMechanics.dynamic.DynamicGameObject;
import com.example.bomberman.service.tick.gameMechanics.dynamic.Fire;
import com.example.bomberman.service.tick.gameMechanics.staticObj.bonus.Bonus;
import com.example.bomberman.service.tick.gameMechanics.staticObj.bonus.BonusType;
import com.example.bomberman.service.tick.gameMechanics.staticObj.tile.Grass;
import com.example.bomberman.service.tick.gameMechanics.staticObj.tile.Material;
import com.example.bomberman.service.tick.gameMechanics.Rect;
import com.example.bomberman.service.tick.gameMechanics.Size;
import com.example.bomberman.service.tick.gameMechanics.dynamic.Bomb;
import com.example.bomberman.service.tick.gameMechanics.staticObj.tile.Tile;
import com.example.bomberman.repos.GameEntityRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Pawn extends DynamicGameObject {

    // Moving speed
    @JsonIgnore
    protected int velocity = 2;

    // Max number of bombs user can spawn
    @JsonIgnore
    protected int bombsMax = 1;

    // How far the fire reaches when bomb explodes
    @JsonIgnore
    protected int bombStrength = 1;

    protected boolean alive;

    @JsonIgnore
    public final static String TYPE = "Pawn";

    // Current direction
    protected Move direction;

    @JsonIgnore
    Action action;

    // Bomb that player can escape from even when there is a collision
    @JsonIgnore
    protected Bomb escapeBomb = null;

    @JsonIgnore
    protected List<Bomb> bombs = new ArrayList<>();

    @JsonIgnore
    protected static final Size size = new Size(32, 36);

    @JsonIgnore
    private final Logger log = LoggerFactory.getLogger(Pawn.class);


    public Pawn(Action action, GameEntityRepository gameEntityRepository) {
        super(gameEntityRepository, size, TYPE);
        this.alive = true;
        this.action = action;
        //set collision size
        getCollision().setSize(new Size(20, 20));
        setDisplacementCollision(new Vector2(4, 3));
    }

    @Override
    public void init() {

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

            log.debug("Plant bomb \n{}", bomb);
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

            Vector2 dir = rect.getEntityPosition().add(getDirection(i));
            Tile tile = gameEntityRepository.getTile(dir);

            if (tile != null && !(tile instanceof Grass)) {

                if (tile.isColliding(rect)) {
                    log.debug("Detect Wall \n{}\n{}\n{}", tile, this, rect);
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
            log.debug("Detect bomb \n{}\n{}", bomb.get(), this);
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
        for (int i = -1; i < 8; i++) {
            Vector2 dir = collision.getEntityPosition().add(getDirection(i));
            Optional<Fire> fire = gameEntityRepository.getFires(dir).stream().filter(e -> e.getBomb().isExploded()).findFirst();
            if (fire.isPresent() && isColliding(fire.get())) {
                log.debug("Detect fire \n{}\n{}", fire, this);
                return true;
            }
        }
        return false;
    }

    public void fireCollision() {
        die();
    }

    /**
     * Checks whether we have got bonus and applies it.
     */
    public void handleBonusCollision() {
        for (int i = -1; i < 8; i++) {
            Vector2 dir = collision.getEntityPosition().add(getDirection(i));

            Tile tile = gameEntityRepository.getTile(dir);

            if (tile == null || tile instanceof Grass) {

                Optional<Bonus> bonus = gameEntityRepository.getBonuses(dir).stream().findFirst();

                bonus.ifPresent(value -> log.debug("bonus.isPresent() {}", value));

                if (bonus.isPresent() &&
                        isColliding(bonus.get())) {
                    applyBonus(bonus.get());
                    log.debug("Apply bonus \n{}\n{}", bonus, this);
                    bonus.get().destroy();
                }
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
        direction = Move.IDLE;
        log.debug("Die pawn {}", this);
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
    public void setBitmapPosition(Vector2 bitmapPosition) {
        if (Objects.equals(bitmapPosition, getBitmapPosition())) {
            return;
        }
        super.setBitmapPosition(bitmapPosition);
    }

    @Override
    public void setEntityPosition(Vector2 entityPosition) {
        if (Objects.equals(entityPosition, getEntityPosition())) {
            return;
        }
        super.setEntityPosition(entityPosition);
    }
}
