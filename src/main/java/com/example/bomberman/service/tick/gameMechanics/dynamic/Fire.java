package com.example.bomberman.service.tick.gameMechanics.dynamic;

import com.example.bomberman.service.tick.gameMechanics.FinalPositionGameEntity;
import com.example.bomberman.service.tick.gameMechanics.Size;
import com.example.bomberman.repos.GameEntityRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Fire extends FinalPositionGameEntity {

    // The bomb that triggered this fire
    private final Bomb bomb;

    @JsonIgnore
    public final static String TYPE = "Fire";

    @JsonIgnore
    private static final Size size = new Size(38, 38);

    public Fire(GameEntityRepository gameEntityRepository, Bomb bomb) {
        super(gameEntityRepository, size, TYPE);
        this.bomb = bomb;
        getCollision().setSize(new Size(32,32));
    }

    @Override
    public void update(long elapsed) {
        if (bomb.isExploded()) {
            this.remove();
        }
    }

    public void remove() {
        if (this.bomb.explodeListener != null) {
            this.bomb.explodeListener.apply(null);
            gameEntityRepository.removeBomb(this.bomb);
            this.bomb.explodeListener = null;
        }

        gameEntityRepository.removeFire(this);

    }

    public Bomb getBomb() {
        return bomb;
    }
}
