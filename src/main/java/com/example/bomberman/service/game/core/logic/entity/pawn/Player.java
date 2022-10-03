package com.example.bomberman.service.game.core.logic.entity.pawn;

import com.example.bomberman.model.Action;
import com.example.bomberman.model.Move;
import com.example.bomberman.service.game.core.Ticker;
import com.example.bomberman.service.game.core.logic.Vector2;
import com.example.bomberman.service.game.core.logic.Rect;
import com.example.bomberman.repos.GameEntityRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Player extends Pawn {
    //duration of action
    @JsonIgnore
    protected static final int INERTIA_TIME = 70;

    // player namePlayer
    @JsonIgnore
    protected final String name;

    @JsonIgnore
    protected final Action action;

    @JsonIgnore
    protected int timer = 0;
    @JsonIgnore
    protected int timerMax = 10_000;
    //the rest of the duration
    @JsonIgnore
    protected int inertia = INERTIA_TIME;
    @JsonIgnore
    private final Logger log = LoggerFactory.getLogger(Player.class);

    public Player(@NotNull Action action,@NotNull GameEntityRepository gameEntityRepository,@NotNull String name) {
        super(gameEntityRepository);
        this.name = name;
        this.action = action;
    }

    @Override
    public void update(long elapsed) {

        if (!this.alive) {
            return;
        }

        boolean plantBomb;
        int velocity = (int) (this.velocity * elapsed/Ticker.FRAME_TIME);
        Move tmpDir;

        synchronized (action) {
            tmpDir = action.getMove();
            plantBomb = action.isPlantBomb();
            action.resetAction();
        }

        if (inertia > 0 && direction != Move.IDLE && tmpDir == Move.IDLE) {
            inertia -= elapsed;
        } else {
            direction = tmpDir;
            inertia = INERTIA_TIME;
        }


        this.timer += elapsed;
        if (plantBomb || this.timer > this.timerMax) {
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
