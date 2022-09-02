package com.example.bomberman.service.tick.gameMechanics.staticObj.tile;

import com.example.bomberman.repos.GameEntityRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Wood extends Tile {
    @JsonIgnore
    public final static String TYPE = "Wood";

    public Wood(GameEntityRepository gameEntityRepository) {
        super(gameEntityRepository, TYPE);
    }

    @Override
    public void init() {

    }

    @Override
    public void update() {

    }
}
