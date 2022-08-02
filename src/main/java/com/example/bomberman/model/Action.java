package com.example.bomberman.model;

public class Action {
    boolean up = false;
    boolean down = false;
    boolean left = false;
    boolean right = false;
    boolean plantBomb = false;
    boolean jump = false;

    synchronized public boolean isUp() {
        boolean tmp = this.up;
        this.up = false;
        return tmp;
    }

    synchronized public void up() {
        this.up = true;
    }

    synchronized public boolean isDown() {
        boolean tmp = this.down;
        this.down = false;
        return tmp;
    }

    synchronized public void down() {
        this.down = true;
    }

    synchronized public boolean isPlantBomb() {
        boolean tmp = this.plantBomb;
        this.plantBomb = false;
        return tmp;
    }

    synchronized public void plantBomb() {
        this.plantBomb = true;
    }

    synchronized public boolean isJump() {
        boolean tmp = this.jump;
        this.jump = false;
        return tmp;
    }

    synchronized public void jump() {
        this.jump = true;
    }

    synchronized public boolean isLeft() {
        boolean tmp = this.left;
        this.left = false;
        return tmp;
    }

    synchronized public void left() {
        this.left = true;
    }

    synchronized public boolean isRight() {
        boolean tmp = this.right;
        this.right = false;
        return tmp;
    }

    synchronized public void right() {
        this.right = true;
    }

    public void move(Move move) {
        switch (move) {
            case UP -> up();
            case DOWN -> down();
            case LEFT -> left();
            case RIGHT -> right();
        }
    }
}
