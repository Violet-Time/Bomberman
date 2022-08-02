package com.example.bomberman.model.game;

import com.example.bomberman.tick.Game.GameEngine;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class Rect implements Collider {

    @JsonIgnore
    private Logger log = LoggerFactory.getLogger(Rect.class);
    /**
     * Entity position on map grid pixels
     */
    @JsonIgnore
    private Vector2 bitmapPosition;

    /**
     * Entity position on map grid
     */
    @JsonIgnore
    private Vector2 entityPosition;

    /**
     * Bitmap dimensions
     */

    protected Size size;

    /*@JsonIgnore
    protected final int border = 5;*/

    public Rect(Size size) {
        this.size = size;
    }

    public Rect(Rect rect) {
        this.size = rect.size;
        this.bitmapPosition = rect.bitmapPosition;
        this.entityPosition = rect.entityPosition;
    }

    public Vector2 getBitmapPosition() {
        return bitmapPosition;
    }

    public void setBitmapPosition(Vector2 bitmapPosition) {
        this.entityPosition = convertToEntityPosition(bitmapPosition);
        this.bitmapPosition = bitmapPosition;
    }

    public Vector2 getEntityPosition() {
        return entityPosition;
    }

    public void setEntityPosition(Vector2 entityPosition) {
        this.bitmapPosition = convertToBitmapPosition(entityPosition);
        this.entityPosition = entityPosition;
    }

    public Vector2 getCentralBitmapPosition() {
        return new Vector2((bitmapPosition.getX() + size.getWight() / 2), (bitmapPosition.getY() + size.getHeight() / 2));
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public boolean comparePositions(Rect other) {
        return comparePositions(other.getBitmapPosition());
    }

    public boolean comparePositions(Vector2 other) {
        return this.bitmapPosition.isColliding(other);
    }

    public boolean compareEntityPositions(Rect other) {
        return comparePositions(other.getBitmapPosition());
    }

    public boolean compareEntityPositions(Vector2 other) {
        return this.entityPosition.isColliding(other);
    }

    /**
     * Convert bitmap pixels position to entity on grid position.
     */
    private Vector2 convertToEntityPosition(Vector2 pixels) {
        Vector2 position = new Vector2(Math.round((pixels.getX() + size.getWight() / 2) / GameEngine.tileSize),
                Math.round((pixels.getY() + size.getHeight() / 2) / GameEngine.tileSize));
        return position;
    }

    /**
     * Convert entity on grid position to bitmap pixels position.
     */
    public static Vector2 convertToBitmapPosition(Vector2 entity) {
        Vector2 position = new Vector2(Math.round(entity.getX() * GameEngine.tileSize),
                Math.round(entity.getY() * GameEngine.tileSize));
        return position;
    }

    @Override
    public boolean isColliding(Collider other) {
        if (other == null) {
            return false;
        }

        log.debug("{" + this + "\n" + other + "}");

        if (getClass() == other.getClass()) {
            return isCollidingGameEntity((Rect) other);
        }

        if (other.getClass() == Vector2.class) {
            return isCollidingPoint((Vector2) other);
        }

        return other.isColliding(this);

        /*if (equals(other)) {
            return true;
        }

        if (other instanceof Rect) {
            Rect gameEntity = (Rect) other;
            return isCollidingGameEntity(gameEntity);
        }

        if (other instanceof Point) {
            Point point = (Point) other;
            return isCollidingPoint(point);
        }

        return false;*/
    }

    protected boolean isCollidingGameEntity(Rect gameEntity) {
        /*if (this.position..getLeft() > b.getRight() ||
                a.getRight() < b.getLeft()) {
            return false;
        }

        return a.getBottom() <= b.getTop() &&
                a.getTop() >= b.getBottom();*/

        if (this.bitmapPosition.getX() >= (gameEntity.bitmapPosition.getX() + gameEntity.size.getHeight()) ||
                (this.bitmapPosition.getX() + this.size.getHeight()) <= gameEntity.bitmapPosition.getX()) {
            return false;
        }

        return this.bitmapPosition.getY() < (gameEntity.bitmapPosition.getY() + gameEntity.size.getWight()) &&
                (this.bitmapPosition.getY() + this.size.getWight()) > gameEntity.bitmapPosition.getY();
    }

    protected boolean isCollidingPoint(Vector2 vector2) {

        if (this.bitmapPosition.getX() >= vector2.getX() ||
                (this.bitmapPosition.getX() + this.size.getHeight()) <= vector2.getX()) {
            return false;
        }

        return this.bitmapPosition.getY() < vector2.getY() &&
                (this.bitmapPosition.getY() + this.size.getWight()) > vector2.getY();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rect rect = (Rect) o;
        return Objects.equals(bitmapPosition, rect.bitmapPosition) && Objects.equals(entityPosition, rect.entityPosition) && Objects.equals(size, rect.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bitmapPosition, entityPosition, size);
    }

    @Override
    public String toString() {
        return "Rect{" +
                "bitmapPosition=" + bitmapPosition +
                ", entityPosition=" + entityPosition +
                ", size=" + size +
                '}';
    }
}
