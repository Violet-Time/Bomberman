package com.example.bomberman.model.game.staticObj;

import com.example.bomberman.model.game.GameEntity;
import com.example.bomberman.model.game.Size;
import com.example.bomberman.repos.GameEntityRepository;

public abstract class StaticGameObject extends GameEntity {
    public StaticGameObject(GameEntityRepository gameEntityRepository, Size size, String type) {
        super(gameEntityRepository, size, type);
    }
}
