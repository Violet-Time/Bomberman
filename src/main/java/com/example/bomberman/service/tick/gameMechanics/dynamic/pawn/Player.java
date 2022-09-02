package com.example.bomberman.service.tick.gameMechanics.dynamic.pawn;

import com.example.bomberman.model.Action;
import com.example.bomberman.model.Move;
import com.example.bomberman.service.tick.Ticker;
import com.example.bomberman.service.tick.gameMechanics.Vector2;
import com.example.bomberman.service.tick.gameMechanics.Rect;
import com.example.bomberman.repos.GameEntityRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Player extends Pawn {

    // player name
    @JsonIgnore
    protected final String name;

    @JsonIgnore
    protected int timer = 0;
    @JsonIgnore
    protected int timerMax = 10;
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

        if (action.isUp()) {
            direction = Move.UP;;
        } else if (action.isDown()) {
            direction = Move.DOWN;
        } else if (action.isLeft()) {
            direction = Move.LEFT;
        } else if (action.isRight()) {
            direction = Move.RIGHT;
        } else {
            direction = Move.IDLE;
        }


        this.timer++;
        if (action.isPlantBomb() || this.timer > this.timerMax * Ticker.FPS) {
            timer = 0;
            plantBomb();
        }

        if (direction.getDirection().getX() != 0 || direction.getDirection().getY() != 0) {

            log.debug("Dir " + direction.getDirection());

            Rect rect = new Rect(this.collision);
            rect.setBitmapPosition(rect.getBitmapPosition().add(direction.getDirection().mul(velocity)));

            log.debug("Rect " + rect);

            if (!detectBombCollision(rect)) {
                if (detectWallCollision(rect)) {
                    // If we are on the corner, move to the aisle
                    Vector2 cornerFix = getCornerFix(direction.getDirection());

                    log.debug("CornerFix " + cornerFix);

                    if (cornerFix != null) {
                        setBitmapPosition(getBitmapPosition().add(cornerFix.mul(velocity)));
                    }

                } else {
                    setBitmapPosition(getBitmapPosition().add(direction.getDirection().mul(velocity)));
                }
            }
        }

        if (detectFireCollision()) {
            die();
        }

        handleBonusCollision();
    }

    @Override
    public void die() {
        super.die();
        log.debug("Die Player {}", name);
        if (gameEntityRepository.removePlayer(this)) {
            log.debug("Remove Player {}", name);
        }
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
