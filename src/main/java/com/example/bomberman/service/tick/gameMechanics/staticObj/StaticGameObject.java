package com.example.bomberman.service.tick.gameMechanics.staticObj;

import com.example.bomberman.service.tick.gameMechanics.GameEntity;
import com.example.bomberman.service.tick.gameMechanics.Size;
import com.example.bomberman.repos.GameEntityRepository;

public abstract class StaticGameObject extends GameEntity {
    public StaticGameObject(GameEntityRepository gameEntityRepository, Size size, String type) {
        super(gameEntityRepository, size, type);
    }
}
