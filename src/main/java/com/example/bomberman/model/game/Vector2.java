package com.example.bomberman.model.game;

import lombok.Data;

import java.util.Objects;

@Data
public final class Vector2 implements Collider {
    
    // fields
    // and methods

    private final int x;
    private final int y;

    public Vector2(Vector2 vector2) {
        this(vector2.getX(), vector2.getY());
    }

    public Vector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2 addX(int x) {
        return add(x, 0);
    }

    public Vector2 addY(int y) {
        return add(0, y);
    }

    public Vector2 add(Vector2 vector2) {
        return add(vector2.getX(), vector2.getY());
    }

    public Vector2 add(int x, int y) {
        return new Vector2(this.x + x, this.y + y);
    }

    public Vector2 subX(int x) {
        return sub(x, 0);
    }

    public Vector2 subY(int y) {
        return sub(0, y);
    }

    public Vector2 sub(Vector2 vector2) {
        return sub(vector2.getX(), vector2.getY());
    }
    public Vector2 sub(int x, int y) {
        return new Vector2(this.x - x, this.y - y);
    }

    public Vector2 mul(int a) {
        return new Vector2(this.x * a, this.y * a);
    }

    public int magnitude() {
        return (int) Math.sqrt(Math.pow(x, 2) + Math.pow(y,2));
    }

    /**
     * @param o - other object to check equality with
     * @return true if two points are equal and not null.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        // cast from Object to Point
        Vector2 vector2 = (Vector2) o;

        return this.x == vector2.getX() && this.y == vector2.getY();
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public boolean isColliding(Collider other) {
        if (other == null || other instanceof Vector2) {
            return equals(other);
        }

        return other.isColliding(this);

        /*if (other instanceof GameEntity) {
            throw new RuntimeException("GameEntity try colliding with Point!");
        }

        return false;*/
    }
}
