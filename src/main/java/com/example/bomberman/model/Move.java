package com.example.bomberman.model;

import com.example.bomberman.service.game.core.logic.Vector2;

public enum Move {
    UP(new Vector2(0, 1)), DOWN(new Vector2(0, -1)), LEFT(new Vector2(-1, 0)),
    RIGHT(new Vector2(1, 0)), PLANT_BOMB(new Vector2(0, 0)), IDLE(new Vector2(0, 0));

    // Current axis direction
    private final Vector2 direction;

    Move(Vector2 direction) {
        this.direction = direction;
    }

    public Vector2 getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return "Move{" +
                "direction=" + direction +
                '}';
    }
}
