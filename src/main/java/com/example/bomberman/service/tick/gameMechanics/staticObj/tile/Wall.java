package com.example.bomberman.service.tick.gameMechanics.staticObj.tile;

import com.example.bomberman.repos.GameEntityRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Wall extends Tile {
    @JsonIgnore
    public final static String TYPE = "Wall";

    public Wall(GameEntityRepository gameEntityRepository) {
        super(gameEntityRepository, TYPE);
    }
}
