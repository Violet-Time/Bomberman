package com.example.bomberman.service.tick.gameMechanics.staticObj.tile;

import com.example.bomberman.service.tick.gameMechanics.FinalPositionGameEntity;
import com.example.bomberman.service.tick.gameMechanics.Size;
import com.example.bomberman.repos.GameEntityRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class Tile extends FinalPositionGameEntity {
    @JsonIgnore
    private static final Size size = new Size(32, 32);
    public Tile(GameEntityRepository gameEntityRepository, String type) {
        super(gameEntityRepository, size, type);
    }

    public void remove() {
        gameEntityRepository.removeTile(this);
    }

}
