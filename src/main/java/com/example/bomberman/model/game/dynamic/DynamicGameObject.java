package com.example.bomberman.model.game.dynamic;

import com.example.bomberman.model.game.GameEntity;
import com.example.bomberman.model.game.Size;
import com.example.bomberman.repos.GameEntityRepository;

public abstract class DynamicGameObject extends GameEntity {
    public DynamicGameObject(GameEntityRepository gameEntityRepository, Size size, String type) {
        super(gameEntityRepository, size, type);
    }
}
