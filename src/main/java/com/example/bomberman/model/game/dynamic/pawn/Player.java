package com.example.bomberman.model.game.dynamic.pawn;

import com.example.bomberman.model.Action;
import com.example.bomberman.model.Move;
import com.example.bomberman.model.game.Vector2;
import com.example.bomberman.repos.GameEntityRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Player extends Pawn {
    protected final String name;
    @JsonIgnore
    private Logger log = LoggerFactory.getLogger(Player.class);

    public Player(Action action, GameEntityRepository gameEntityRepository, String name) {
        super(action, gameEntityRepository);
        this.name = name;
    }

    @Override
    public void update() {
        //log.debug(toString());
        if (!this.alive) {
            return;
        }

        Vector2 entityVector;

        //Vector2 dir;

        if (action.isUp()) {
            entityVector = up();
            //dir = new Vector2(0, 1);
        } else if (action.isDown()) {
            entityVector = down();
            //dir = new Vector2(0, -1);
        } else if (action.isLeft()) {
            entityVector = left();
            //dir = new Vector2(-1, 0);
        } else if (action.isRight()) {
            entityVector = right();
            //dir = new Vector2(1, 0);
        } else {
            entityVector = idle();
            //dir = new Vector2(0, 0);
        }

        if (action.isPlantBomb()) {
            plantBomb();
        }

        if (entityVector.getX() != 0 || entityVector.getY() != 0/*pixels.getX() != getBitmapPosition().getX() || pixels.getY() != getBitmapPosition().getY()*/) {
            if (!detectBombCollision(getBitmapPosition().add(entityVector.mul(velocity)))) {
                if (detectWallCollision(entityVector.mul(velocity))) {
                    // If we are on the corner, move to the aisle
                    Vector2 cornerFix = getCornerFix(entityVector);
                    log.warn(cornerFix != null ? cornerFix.toString() : null);
                    if (cornerFix != null) {

                        setBitmapPosition(getBitmapPosition().add(cornerFix.mul(velocity)));
                        /*int fixX = 0;
                        int fixY = 0;
                        if (entityVector.getX() != 0) {
                            fixY = (cornerFix.getY() - getBitmapPosition().getY()) > 0 ? 1 : -1;
                        } else {
                            fixX = (cornerFix.getX() - getBitmapPosition().getX()) > 0 ? 1 : -1;
                        }
                        setBitmapPosition(new Vector2(getBitmapPosition().getX() + fixX * this.velocity, getBitmapPosition().getY() + fixY * this.velocity));*/
                    }
                } else {
                    setBitmapPosition(getBitmapPosition().add(entityVector.mul(velocity)));
                }
            }
        }



        if (detectFireCollision()) {
            die();
        }

        handleBonusCollision();
    }

    public Vector2 up() {
        Vector2 pixels = new Vector2(0, 1);
        direction = Move.UP;
        return pixels;
    }

    public Vector2 down() {
        Vector2 pixels = new Vector2(0, -1);
        direction = Move.DOWN;
        return pixels;
    }
    public Vector2 left() {
        Vector2 pixels = new Vector2(-1, 0);
        direction = Move.LEFT;
        return pixels;
    }

    public Vector2 right() {
        Vector2 pixels = new Vector2(1, 0);
        direction = Move.RIGHT;
        return pixels;
    }

    public Vector2 idle() {
        Vector2 pixels = new Vector2(0, 0);
        direction = Move.IDLE;
        return pixels;
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
