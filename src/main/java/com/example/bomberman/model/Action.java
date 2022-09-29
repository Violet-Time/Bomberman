package com.example.bomberman.model;

public class Action {
    private boolean up = false;
    private boolean down = false;
    private boolean left = false;
    private boolean right = false;
    private boolean plantBomb = false;
    private boolean jump = false;

    public synchronized boolean isPlantBomb() {
        return this.plantBomb;
    }

    public synchronized void plantBomb() {
        this.plantBomb = true;
    }

    public synchronized boolean isJump() {
        return this.jump;
    }

    public synchronized void jump() {
        this.jump = true;
    }

    public synchronized void setMove(Move move) {
        switch (move) {
            case UP -> this.up = true;
            case DOWN -> this.down = true;
            case LEFT -> this.left = true;
            case RIGHT -> this.right = true;
        }
    }

    public synchronized Move getMove() {
        if (up) {
            return Move.UP;
        }
        if (down) {
            return Move.DOWN;
        }
        if (left) {
            return Move.LEFT;
        }
        if (right) {
            return Move.RIGHT;
        }

        return Move.IDLE;
    }

    public void resetAction() {
        up = false;
        down = false;
        left = false;
        right = false;
        plantBomb = false;
        jump = false;
    }
}
