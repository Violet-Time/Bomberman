package com.example.bomberman.model.game.staticObj.tile;

import com.example.bomberman.repos.GameEntityRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Wood extends Tile {
    @JsonIgnore
    public final static String TYPE = "Wood";

    public Wood(GameEntityRepository gameEntityRepository) {
        super(gameEntityRepository, TYPE);
    }
}
