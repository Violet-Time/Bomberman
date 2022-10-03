package com.example.bomberman.service.game.core.logic.entity.pawn;

import com.example.bomberman.model.Move;
import com.example.bomberman.service.game.core.Ticker;
import com.example.bomberman.service.game.core.logic.Vector2;
import com.example.bomberman.service.game.core.logic.entity.Fire;
import com.example.bomberman.service.game.core.logic.entity.bonus.Bonus;
import com.example.bomberman.service.game.core.logic.entity.tile.Material;
import com.example.bomberman.service.game.core.logic.Rect;
import com.example.bomberman.service.game.core.logic.entity.Bomb;
import com.example.bomberman.service.game.core.logic.entity.tile.Tile;
import com.example.bomberman.repos.GameEntityRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Bot extends Pawn {

    // Target position on map we are heading to
    @JsonIgnore
    Vector2 targetPosition;

    // Target bitmap position on map we are heading to
    @JsonIgnore
    Vector2 targetBitmapPosition;
    @JsonIgnore
    int startTimerMax = 60;
    @JsonIgnore
    int startTimer = 0;
    @JsonIgnore
    boolean started = false;
    @JsonIgnore
    boolean wait = false;

    @JsonIgnore
    private final Logger log = LoggerFactory.getLogger(Bot.class);

    public Bot(GameEntityRepository gameEntityRepository) {
        super(gameEntityRepository);
        this.direction = Move.IDLE;
        this.startTimerMax = (int) (Math.random() * 60);
    }

    @Override
    public void update(long elapsed) {

        if (!isAlive()) {
            return;
        }

        if (getBitmapPosition() == null || getEntityPosition() == null) {
            throw new NullPointerException("position is null");
        }

        if (this.targetBitmapPosition == null) {
            this.targetBitmapPosition = getBitmapPosition();
        }

        if (this.targetPosition == null) {
            this.targetPosition = getEntityPosition();
        }

        this.wait = false;

        if (!this.started && this.startTimer < this.startTimerMax) {
            this.startTimer++;
            if (this.startTimer >= this.startTimerMax) {
                this.started = true;
            }

            this.direction = Move.IDLE;
            this.wait = true;
        }

        Vector2 bitmapPosition = this.getBitmapPosition();

        if (this.targetBitmapPosition.getX() == bitmapPosition.getX() && this.targetBitmapPosition.getY() == bitmapPosition.getY()) {

            // If we bumped into the wood, burn it!
            // If we are near player, kill it!
            if (getNearWood() != null || wantKillPlayer()) {
                plantBomb();
            }

            // When in safety, wait until explosion
            if (!this.bombs.isEmpty()) {
                if (!isFire(getEntityPosition())) {
                    this.direction = Move.IDLE;
                    this.wait = true;
                }
            }

            if (!this.wait) {
                findTargetPosition();
            }
        }

        if (!this.wait) {
            moveToTargetPosition(elapsed);
        }

        handleBonusCollision();

        if (detectFireCollision()) {
            // Bot has to die
            die();
        }

    }

    /**
     * Finds the next tile position where we should move.
     */
    public void findTargetPosition() {

        List<Vector2> targets = getPossibleTargets();
        // Do not go the same way if possible
        if (targets.size() > 1) {
            Vector2 previousPosition = getPreviousPosition();
            targets.removeIf(item -> item.equals(previousPosition));
        }

        this.targetPosition = getRandomTarget(targets);
        if (this.targetPosition != null) {
            loadTargetPosition(this.targetPosition);
            this.targetBitmapPosition = convertToBitmapPosition(this.targetPosition);
            log.debug("id={} findTargetPosition={}", id, targetBitmapPosition);
        }
    }

    /**
     * Moves a step forward to target position.
     */
    public void moveToTargetPosition(long elapsed) {

        int velocity = (int) (this.velocity * elapsed/Ticker.FRAME_TIME);
        int distanceX = Math.abs(this.targetBitmapPosition.getX() - getBitmapPosition().getX());
        int distanceY = Math.abs(this.targetBitmapPosition.getY() - getBitmapPosition().getY());
        if (distanceX > 0 && distanceX < this.velocity) {
            velocity = distanceX;
        } else if (distanceY > 0 && distanceY < this.velocity) {
            velocity = distanceY;
        }

        Rect targetPosition = new Rect(collision);
        targetPosition.setBitmapPosition(getBitmapPosition().add(direction.getDirection().mul(velocity)));
        if (!this.detectWallCollision(targetPosition)) {
            setBitmapPosition(targetPosition.getBitmapPosition());
        }
    }

    /**
     * Returns near grass tiles.
     */
    public List<Vector2> getPossibleTargets() {
        List<Vector2> targets = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Vector2 dir = getDirection(i);

            Vector2 position = getEntityPosition().add(dir);
            if (gameEntityRepository.getTileMaterial(position) == Material.GRASS && !hasBomb(position)) {
                targets.add(position);
            }
        }

        List<Vector2> safeTargets = new ArrayList<>();
        for (Vector2 target : targets) {
            if (!isFire(target)) {
                safeTargets.add(target);
            }
        }

        boolean isLucky = Math.random() > 0.3;
        return safeTargets.size() > 0 && isLucky ? safeTargets : targets;
    }

    /**
     * Loads vectors and animation namePlayer for target position.
     */
    public void loadTargetPosition(Vector2 position) {

        Vector2 gPosition = getEntityPosition();
        Vector2 dir = position.sub(gPosition);

        if (dir.getX() == 1 && dir.getY() == 0) {
            this.direction = Move.RIGHT;
        } else if (dir.getX() == -1 && dir.getY() == 0) {
            this.direction = Move.LEFT;
        } else if (dir.getX() == 0 && dir.getY() == 1) {
            this.direction = Move.UP;
        } else if (dir.getX() == 0 && dir.getY() == -1) {
            this.direction = Move.DOWN;
        }
        log.debug("id={} loadTargetPosition={}", id, direction.name());
    }

    /**
     * Gets previous position by current position and direction vector.
     */
    public Vector2 getPreviousPosition() {
        return this.targetPosition.sub(this.direction.getDirection());
    }

    /**
     * Returns random item from array.
     */
    public Vector2 getRandomTarget(List<Vector2> targets) {
        if (targets.size() > 0) {
            return targets.get((int) Math.floor(Math.random() * targets.size()));
        }
        return null;
    }
    @Override
    public void applyBonus(Bonus bonus) {
        super.applyBonus(bonus);

        // It is too dangerous to have more bombs available
        this.bombsMax = 1;
    }

    /**
     * Game is over when no bots and one player left.
     */
    @Override
    public void die() {
        super.die();
        log.debug("id={} die", id);
        if (gameEntityRepository.removeBot(this)) {
            log.debug("Remove Bot");
        }
    }

    /**
     * Checks whether there is any wood around.
     */
    public Tile getNearWood() {
        for (int i = 0; i < 4; i++) {
            Vector2 dir = getDirection(i);

            Vector2 entityPosition = getEntityPosition().add(dir);

            if (gameEntityRepository.getTileMaterial(entityPosition) == Material.WOOD) {
                return gameEntityRepository.getTile(entityPosition);
            }
        }
        return null;
    }

    /**
     * Checks whether player is near. If yes and we are angry, return true.
     */
    public boolean wantKillPlayer() {
        boolean isNear = false;

        for (int i = 0; i < 4; i++) {
            Vector2 dir = getDirection(i);

            Vector2 entityPosition = this.getEntityPosition().add(dir);

            Player player = gameEntityRepository.getPlayers(entityPosition).stream().findFirst().orElse(null);

            if (player != null && player.isAlive()) {
                isNear = true;
                break;
            }

        }

        boolean isAngry = Math.random() > 0.5;

        return isNear && isAngry;
    }

    /**
     * Places the bomb in current position
     */
    @Override
    public void plantBomb() {

        if (!gameEntityRepository.getBombs(getEntityPosition()).isEmpty()) {
            return;
        }

        if (this.bombs.size() < this.bombsMax) {
            Bomb bomb = new Bomb(gameEntityRepository, this.bombStrength);
            bomb.setEntityPosition(getEntityPosition());
            this.bombs.add(bomb);
            gameEntityRepository.addBomb(bomb);

            bomb.setExplodeListener((e) -> {
                this.bombs.remove(bomb);
                this.wait = false;
                return e;
            });
        }
    }

    /**
     * Checks whether position is safe  and possible explosion cannot kill us.
     */
    public boolean isSafe(Rect rect) {
        for (int i = 0; i < 4; i++) {
            Vector2 dir = getDirection(i);
            Fire fire = gameEntityRepository.getFires(rect.getEntityPosition().add(dir)).stream().findFirst().orElse(null);
            if (fire != null && rect.isColliding(fire)) {
                log.debug("id={} isSafe fire={} rect={}", id, fire, rect);
                log.debug(fire.getEntityPosition().toString());
                return false;
            }
        }
        return true;
    }
    public boolean isFire(Vector2 position) {
        Fire fire = gameEntityRepository.getFires(position).stream().findFirst().orElse(null);
        return fire != null;
    }

    public boolean hasBomb(Vector2 position) {
        return !gameEntityRepository.getBombs(position).isEmpty();
    }

    @Override
    public String toString() {
        return "Bot{" +
                super.toString() +
                '}';
    }
}
