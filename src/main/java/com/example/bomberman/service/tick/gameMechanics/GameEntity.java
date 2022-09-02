package com.example.bomberman.service.tick.gameMechanics;

import com.example.bomberman.model.Move;
import com.example.bomberman.repos.GameEntityRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public abstract class GameEntity extends Rect {

    protected final long id;

    protected final String type;
    @JsonIgnore
    protected final Rect collision;

    @JsonIgnore
    protected Vector2 displacementCollision;

    @JsonIgnore
    protected final GameEntityRepository gameEntityRepository;
    @JsonIgnore
    private final Logger log = LoggerFactory.getLogger(GameEntity.class);

    public GameEntity(GameEntityRepository gameEntityRepository, Size size, String type) {
        super(size);
        this.id = gameEntityRepository.generateId();
        this.collision = new Rect(size);
        this.type = type;
        this.gameEntityRepository = gameEntityRepository;
        //super.setBitmapPosition(new Vector2(0, 0));

        this.displacementCollision = new Vector2(0,0);
    }




    abstract public void init();

    abstract public void update();

    public long getId() {
        return id;
    }

    public Rect getCollision() {
        return collision;
    }

    public Vector2 getDisplacementCollision() {
        return displacementCollision;
    }

    public void setDisplacementCollision(Vector2 displacementCollision) {
        this.displacementCollision = displacementCollision;
    }

    @Override
    public Vector2 getEntityPosition() {
        return super.getEntityPosition();
    }

    @Override
    public Vector2 getBitmapPosition() {
        return super.getBitmapPosition();
    }

    @Override
    public void setBitmapPosition(Vector2 bitmapPosition) {
        if (bitmapPosition == null) {
            log.warn("setBitmapPosition: bitmapPosition == null");
            return;
        }

        if (Objects.equals(bitmapPosition, getBitmapPosition())) {
            return;
        }

        log.debug("entityPosition: {}\n{}", getEntityPosition(), convertToEntityPosition(bitmapPosition));

        if (!Objects.equals(getEntityPosition(), convertToEntityPosition(bitmapPosition))) {
            log.debug("convertToEntityPosition: {}", convertToEntityPosition(bitmapPosition));
            gameEntityRepository.removeGameEntity(this);
            super.setBitmapPosition(bitmapPosition);
            collision.setBitmapPosition(getBitmapPosition().add(displacementCollision));
            gameEntityRepository.addGameEntity(this);
        } else {
            super.setBitmapPosition(bitmapPosition);
            collision.setBitmapPosition(getBitmapPosition().add(displacementCollision));
        }
    }

    @Override
    public void setEntityPosition(Vector2 entityPosition) {
        if (entityPosition == null) {
            log.warn("setEntityPosition: entityPosition == null");
            return;
        }

        if (Objects.equals(entityPosition, getEntityPosition())) {
            return;
        }

        gameEntityRepository.removeGameEntity(this);
        super.setEntityPosition(entityPosition);
        collision.setBitmapPosition(getBitmapPosition().add(displacementCollision));
        gameEntityRepository.addGameEntity(this);
    }

    private void updatePosition() {

    }

    public Vector2 getDirection(int i) {
        return switch (i) {
            case 0 -> Move.RIGHT.getDirection();
            case 1 -> Move.LEFT.getDirection();
            case 2 -> Move.UP.getDirection();
            case 3 -> Move.DOWN.getDirection();
            case 4 -> new Vector2(1, 1);
            case 5 -> new Vector2(-1, 1);
            case 6 -> new Vector2(-1, -1);
            case 7 -> new Vector2(1, -1);
            default -> Move.IDLE.getDirection();
        };
    }

    @Override
    public boolean isColliding(Collider other) {
        if (other == null) {
            return false;
        }

        log.debug("Colliding\n{" + this + "\n" + other + "}");

        if (other instanceof GameEntity) {
            return collision.isColliding(((GameEntity) other).collision);
        }

        return collision.isColliding(other);
    }

    @JsonProperty
    public Vector2 getPosition() {
        return getBitmapPosition();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameEntity that = (GameEntity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "GameEntity{" +
                "id=" + id +
                ", collision=" + collision +
                ", type='" + type + '\'' +
                ", size=" + size + '\'' +
                "\n {" + super.toString() + "}" +
                "}";
    }
}
