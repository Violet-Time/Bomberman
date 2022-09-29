package com.example.bomberman.service.tick.gameMechanics.staticObj.bonus;

import com.example.bomberman.service.tick.gameMechanics.FinalPositionGameEntity;
import com.example.bomberman.service.tick.gameMechanics.Size;
import com.example.bomberman.repos.GameEntityRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Bonus extends FinalPositionGameEntity {
    @JsonIgnore
    private static final Size size = new Size(28, 28);
    @JsonIgnore
    public final static String TYPE = "Bonus";

    private final BonusType bonusType;

    public Bonus(GameEntityRepository gameEntityRepository, BonusType bonusType) {
        super(gameEntityRepository, size, TYPE);
        this.bonusType = bonusType;
    }

    public BonusType getBonusType() {
        return bonusType;
    }

    public void destroy() {
        gameEntityRepository.removeBonus(this);
    }
}
