package com.example.bomberman.model.game;

import com.example.bomberman.repos.GameEntityRepository;
import com.example.bomberman.repos.GameObjectRepositoryImpl;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public abstract class GameEntity extends Rect {

    protected final long id;
    @JsonIgnore
    protected final Rect collision;

    protected final String type;

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
    }

    public void init() {
    }

    public void update() {
    }

    public Vector2 star(int i) {
        return switch (i) {
            case 0 -> new Vector2(1, 0);
            case 1 -> new Vector2(-1, 0);
            case 2 -> new Vector2(0, 1);
            case 3 -> new Vector2(0, -1);
            case 4 -> new Vector2(1, 1);
            case 5 -> new Vector2(-1, 1);
            case 6 -> new Vector2(-1, -1);
            case 7 -> new Vector2(1, -1);
            default -> new Vector2(0,0);
        };
    }

    public long getId() {
        return id;
    }

    public Rect getCollision() {
        return collision;
    }

    @Override
    public void setBitmapPosition(Vector2 bitmapPosition) {
        super.setBitmapPosition(bitmapPosition);
        collision.setBitmapPosition(getBitmapPosition());
    }

    @Override
    public void setEntityPosition(Vector2 entityPosition) {
        super.setEntityPosition(entityPosition);
        collision.setBitmapPosition(getBitmapPosition());
    }

    @Override
    public boolean isColliding(Collider other) {
        if (other == null) {
            return false;
        }

        log.debug("Colliding\n{" + this + "\n" + other + "}");

        if (other.getClass() == getClass()) {
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
                " {" + super.toString() + "}" +
                '}';
    }
}
