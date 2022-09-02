package com.example.bomberman.service.tick.gameMechanics.dynamic;

import com.example.bomberman.service.tick.gameMechanics.GameEntity;
import com.example.bomberman.service.tick.gameMechanics.Size;
import com.example.bomberman.repos.GameEntityRepository;

public abstract class DynamicGameObject extends GameEntity {
    public DynamicGameObject(GameEntityRepository gameEntityRepository, Size size, String type) {
        super(gameEntityRepository, size, type);
    }
}
