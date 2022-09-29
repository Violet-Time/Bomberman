package com.example.bomberman.service.tick.gameMechanics;

import com.example.bomberman.repos.GameEntityRepository;

public abstract class FinalPositionGameEntity extends GameEntity {
    public FinalPositionGameEntity(GameEntityRepository gameEntityRepository, Size size, String type) {
        super(gameEntityRepository, size, type);
    }

    @Override
    public void setEntityPosition(Vector2 entityPosition) {
        if (getEntityPosition() == null) {
            super.setEntityPosition(entityPosition);
        }
    }

    @Override
    public void setBitmapPosition(Vector2 bitmapPosition) {
        if (getBitmapPosition() == null) {
            super.setBitmapPosition(bitmapPosition);
        }
    }

    @Override
    public void update(long elapsed) {

    }
}
