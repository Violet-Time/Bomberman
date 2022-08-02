package com.example.bomberman.model.game;

public enum Type {
    BOMB("Bomb");

    private final String name;

    Type(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
