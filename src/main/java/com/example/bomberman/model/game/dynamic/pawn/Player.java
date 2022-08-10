package com.example.bomberman.model.game.dynamic.pawn;

import com.example.bomberman.model.Action;
import com.example.bomberman.model.Move;
import com.example.bomberman.model.game.Rect;
import com.example.bomberman.model.game.Vector2;
import com.example.bomberman.repos.GameEntityRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Player extends Pawn {

    protected final String name;
    @JsonIgnore
    private final Logger log = LoggerFactory.getLogger(Player.class);

    public Player(Action action, GameEntityRepository gameEntityRepository, String name) {
        super(action, gameEntityRepository);
        this.name = name;
    }

    @Override
    public void update() {

        if (!this.alive) {
            return;
        }

        // Current axis direction
        Vector2 dir;

        if (action.isUp()) {
            dir = new Vector2(0, 1);
            direction = Move.UP;;
        } else if (action.isDown()) {
            dir = new Vector2(0, -1);
            direction = Move.DOWN;
        } else if (action.isLeft()) {
            dir = new Vector2(-1, 0);
            direction = Move.LEFT;
        } else if (action.isRight()) {
            dir = new Vector2(1, 0);
            direction = Move.RIGHT;
        } else {
            dir = new Vector2(0, 0);
            direction = Move.IDLE;
        }

        if (action.isPlantBomb()) {
            plantBomb();
        }

        if (dir.getX() != 0 || dir.getY() != 0) {

            log.debug("Dir " + dir);

            Rect rect = new Rect(this.collision);
            rect.setBitmapPosition(rect.getBitmapPosition().add(dir.mul(velocity)));

            log.debug("Rect " + rect);

            if (!detectBombCollision(rect)) {
                if (detectWallCollision(rect)) {
                    // If we are on the corner, move to the aisle
                    Vector2 cornerFix = getCornerFix(dir);

                    log.debug("CornerFix " + cornerFix);

                    if (cornerFix != null) {
                        setBitmapPosition(getBitmapPosition().add(cornerFix.mul(velocity)));
                    }

                } else {
                    setBitmapPosition(getBitmapPosition().add(dir.mul(velocity)));
                }
            }
        }

        if (detectFireCollision()) {
            die();
        }

        handleBonusCollision();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Player{" +
                super.toString() +
                '}';
    }
}
