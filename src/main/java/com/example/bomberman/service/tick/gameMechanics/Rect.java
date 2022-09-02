package com.example.bomberman.service.tick.gameMechanics;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class Rect implements Collider {

    // Entity position on map grid pixels
    @JsonIgnore
    private Vector2 bitmapPosition;

    // Entity position on map grid
    @JsonIgnore
    private Vector2 entityPosition;

    // Bitmap dimensions
    @JsonIgnore
    protected Size size;

    @JsonIgnore
    private final Logger log = LoggerFactory.getLogger(Rect.class);

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

    /**
     * Calculates and updates entity position according to its actual bitmap position
     */
    public void setBitmapPosition(Vector2 bitmapPosition) {
        this.entityPosition = convertToEntityPosition(bitmapPosition);
        this.bitmapPosition = bitmapPosition;
    }

    public Vector2 getEntityPosition() {
        return entityPosition;
    }

    /**
     * Calculates and updates bitmap position according to its actual entity position
     */
    public void setEntityPosition(Vector2 entityPosition) {
        this.bitmapPosition = convertToBitmapPosition(entityPosition);
        this.entityPosition = entityPosition;
    }

    public Vector2 getCentralBitmapPosition() {
        return new Vector2((bitmapPosition.getX() + size.getWeight() / 2), (bitmapPosition.getY() + size.getHeight() / 2));
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
    public Vector2 convertToEntityPosition(Vector2 pixels) {
        return new Vector2((pixels.getX() + size.getWeight() / 2) / GameEngine.TILE_SIZE,
                (pixels.getY() + size.getHeight() / 2) / GameEngine.TILE_SIZE);
    }

    /**
     * Convert entity on grid position to bitmap pixels position.
     */
    public Vector2 convertToBitmapPosition(Vector2 entity) {
        return new Vector2(entity.getX() * GameEngine.TILE_SIZE/*  - size.getWeight() / 2*/,
                entity.getY() * GameEngine.TILE_SIZE/* - size.getHeight() / 2*/);
    }

    /**
     * Checks whether two rectangles intersect.
     */
    @Override
    public boolean isColliding(Collider other) {
        if (other == null) {
            return false;
        }

        if (getClass() == other.getClass()) {
            return isCollidingRect((Rect) other);
        }

        if (other.getClass() == Vector2.class) {
            return isCollidingPoint((Vector2) other);
        }

        return other.isColliding(this);
    }

    protected boolean isCollidingRect(Rect gameEntity) {

        if (this.bitmapPosition.getX() >= (gameEntity.bitmapPosition.getX() + gameEntity.size.getWeight()) ||
                (this.bitmapPosition.getX() + this.size.getWeight()) <= gameEntity.bitmapPosition.getX()) {
            return false;
        }

        return this.bitmapPosition.getY() < (gameEntity.bitmapPosition.getY() + gameEntity.size.getHeight()) &&
                (this.bitmapPosition.getY() + this.size.getHeight()) > gameEntity.bitmapPosition.getY();
    }

    protected boolean isCollidingPoint(Vector2 vector2) {

        if (this.bitmapPosition.getX() >= vector2.getX() ||
                (this.bitmapPosition.getX() + this.size.getWeight()) <= vector2.getX()) {
            return false;
        }

        return this.bitmapPosition.getY() < vector2.getY() &&
                (this.bitmapPosition.getY() + this.size.getHeight()) > vector2.getY();
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
