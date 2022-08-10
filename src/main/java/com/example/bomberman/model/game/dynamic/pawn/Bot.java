package com.example.bomberman.model.game.dynamic.pawn;

import com.example.bomberman.model.Move;
import com.example.bomberman.model.game.Rect;
import com.example.bomberman.model.game.Vector2;
import com.example.bomberman.model.game.dynamic.Bomb;
import com.example.bomberman.model.game.dynamic.Fire;
import com.example.bomberman.model.game.staticObj.bonus.Bonus;
import com.example.bomberman.model.game.staticObj.tile.Material;
import com.example.bomberman.model.game.staticObj.tile.Tile;
import com.example.bomberman.repos.GameEntityRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Bot extends Pawn {


    @JsonIgnore
    Move lastDirection = null;

    /** Directions that are not allowed to go because of collision */
    @JsonIgnore
    List<Move> excludeDirections;

    @JsonIgnore
    Vector2 dir = new Vector2(0, -1);

    /** Target position on map we are heading to */
    @JsonIgnore
    Vector2 previousPosition = new Vector2(0,0);
    @JsonIgnore
    Vector2 targetPosition = new Vector2(0,0);
    @JsonIgnore
    Vector2 targetBitmapPosition = new Vector2(0,0);
    @JsonIgnore
    int bombsMax = 1;
    @JsonIgnore
    boolean wait = false;

    @JsonIgnore
    int startTimerMax = 60;
    @JsonIgnore
    int startTimer = 0;
    @JsonIgnore
    boolean started = false;

    @JsonIgnore
    private final Logger log = LoggerFactory.getLogger(Bot.class);

    public Bot(GameEntityRepository gameEntityRepository, Vector2 entityPosition) {
        super(null, gameEntityRepository);
        setEntityPosition(entityPosition);
        findTargetPosition();
        this.startTimerMax = (int) (Math.random() * 60);
    }

    @Override
    public void update() {
        if (!isAlive()) {
            //fade();
            return;
        }

        this.wait = false;

        if (!this.started && this.startTimer < this.startTimerMax) {
            this.startTimer++;
            if (this.startTimer >= this.startTimerMax) {
                this.started = true;
            }
            //this.animate('idle');
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
                if (this.isSafe(this.getEntityPosition())) {
                    this.direction = Move.IDLE;
                    this.wait = true;
                }
            }

            if (!this.wait) {
                findTargetPosition();
            }
        }

        if (!this.wait) {
            moveToTargetPosition();
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
        //Point target = getEntityPosition().add(dir);
        /*target.addX(this.dirX);
        target.addY(this.dirY);*/

        List<Vector2> targets = getPossibleTargets();
        // Do not go the same way if possible
        if (targets.size() > 1) {
            Vector2 previousPosition = getPreviousPosition();
            targets.removeIf(item -> item.getX() == previousPosition.getX() && item.getY() == previousPosition.getY());
        }
        this.targetPosition = getRandomTarget(targets);
        if (this.targetPosition != null) {
            loadTargetPosition(this.targetPosition);
            this.targetBitmapPosition = convertToBitmapPosition(this.targetPosition);
        }
    }

    /**
     * Moves a step forward to target position.
     */
    public void moveToTargetPosition() {
        //this.animate(this.direction);

        Vector2 position = getBitmapPosition();

        int velocity = this.velocity;
        int distanceX = Math.abs(this.targetBitmapPosition.getX() - getBitmapPosition().getX());
        int distanceY = Math.abs(this.targetBitmapPosition.getY() - getBitmapPosition().getY());
        if (distanceX > 0 && distanceX < this.velocity) {
            velocity = distanceX;
        } else if (distanceY > 0 && distanceY < this.velocity) {
            velocity = distanceY;
        }

        //Vector2 targetPosition = new Vector2(getBitmapPosition().getX() + this.dir.getX() * velocity, getBitmapPosition().getY() + this.dir.getY() * velocity);
        Rect rect = new Rect(collision);
        rect.setBitmapPosition(getBitmapPosition().add(dir.mul(velocity)));
        if (!this.detectWallCollision(rect)) {
            setBitmapPosition(rect.getBitmapPosition());
            /*position.setX(targetPosition.getX());
            position.setY(targetPosition.getY());
            setBitmapPosition(position);*/
        }

        //this.updatePosition();
    }

    /**
     * Returns near grass tiles.
     */
    public List<Vector2> getPossibleTargets() {
        List<Vector2> targets = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Vector2 dir = star(i);

            Vector2 position = getEntityPosition().add(dir);
            if (gameEntityRepository.getTileMaterial(position) == Material.GRASS && !hasBomb(position)) {
                targets.add(position);
            }
        }

        List<Vector2> safeTargets = new ArrayList<>();
        for (Vector2 target : targets) {
            if (isSafe(target)) {
                safeTargets.add(target);
            }
        }

        boolean isLucky = Math.random() > 0.3;
        return safeTargets.size() > 0 && isLucky ? safeTargets : targets;
    }

    /**
     * Loads vectors and animation name for target position.
     */
    public void loadTargetPosition(Vector2 position) {
        Vector2 gPosition = getEntityPosition();
        this.dir = position.sub(gPosition);
        /*this.dirX = position.getX() - gPosition.getX();
        this.dirY = position.getY() - gPosition.getY();*/
        if (this.dir.getX() == 1 && this.dir.getY() == 0) {
            this.direction = Move.RIGHT;
        } else if (this.dir.getX() == -1 && this.dir.getY() == 0) {
            this.direction = Move.LEFT;
        } else if (this.dir.getX() == 0 && this.dir.getY() == 1) {
            this.direction = Move.UP;
        } else if (this.dir.getX() == 0 && this.dir.getY() == -1) {
            this.direction = Move.DOWN;
        }
    }

    /**
     * Gets previous position by current position and direction vector.
     */
    public Vector2 getPreviousPosition() {
        return this.targetPosition.sub(this.dir);
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
        //this._super();
        super.die();
        boolean botsAlive = false;

        if (gameEntityRepository.removeBot(this)) {
            log.debug("Remove Bot");
        }

        // Cache bots
        /*List<Bot> bots = new ArrayList<>(gameObjectRepository.getBots());

        for (Bot bot : bots) {
            // Remove bot
            if (bot == this) {
                gameObjectRepository.removeBot(bot);
            }

            if (bot.alive) {
                botsAlive = true;
            }
        }*/

        /*if (!botsAlive && gameObjectRepository.countPlayersAlive() == 1) {
            gameEngine.gameOver('win');
        }*/
    }

    /**
     * Checks whether there is any wood around.
     */
    public Tile getNearWood() {
        for (int i = 0; i < 4; i++) {
            Vector2 dir = star(i);

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
            Vector2 dir = star(i);

            Vector2 entityPosition = this.getEntityPosition().add(dir);

            Player player = gameEntityRepository.getPlayers(entityPosition).stream().findFirst().orElse(null);

            if (player != null && player.isAlive()) {
                isNear = true;
                break;
            }

        }

        boolean isAngry = Math.random() > 0.5;

        if (isNear && isAngry) {
            return true;
        }

        return false;
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
    public boolean isSafe(Vector2 position) {
        Fire fire = gameEntityRepository.getFires(position).stream().findFirst().orElse(null);
        log.debug("a");
        if (fire != null) {
            log.debug(fire.getEntityPosition().toString());
            return false;
        }

        /*for (Bomb bomb : gameEntityRepository.getAllBombs()) {
            for (Point fire : bomb.getDangerPositions()) {
                log.debug(fire.toString());
                if (fire.isColliding(position)) {
                    return false;
                }
            }
        }*/
        return true;
    }

    public boolean hasBomb(Vector2 position) {
        return !gameEntityRepository.getBombs(position).isEmpty();
        /*
        for (Bomb bomb : gameObjectRepository.getBombs()) {
            if (bomb.comparePositions(position)) {
                return true;
            }
        }
        return false;*/
    }

    @Override
    public String toString() {
        return "Bot{" +
                super.toString() +
                '}';
    }
}
