package com.example.bomberman.model.game.staticObj.bonus;

import com.example.bomberman.model.game.Size;
import com.example.bomberman.model.game.staticObj.StaticGameObject;
import com.example.bomberman.repos.GameEntityRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Bonus extends StaticGameObject {
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
        /*gGameEngine.stage.removeChild(this.bmp);
        Utils.removeFromArray(gGameEngine.bonuses, this);*/
    }
}
