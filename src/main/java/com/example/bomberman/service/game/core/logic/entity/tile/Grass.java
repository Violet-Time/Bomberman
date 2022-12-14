package com.example.bomberman.service.game.core.logic.entity.tile;

import com.example.bomberman.repos.GameEntityRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Grass extends Tile {
    @JsonIgnore
    public final static String TYPE = "Grass";

    public Grass(GameEntityRepository gameEntityRepository) {
        super(gameEntityRepository, TYPE);
    }
}
