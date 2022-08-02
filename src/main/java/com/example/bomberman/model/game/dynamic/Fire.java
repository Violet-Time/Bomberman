package com.example.bomberman.model.game.dynamic;

import com.example.bomberman.model.game.Size;
import com.example.bomberman.repos.GameEntityRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Fire extends DynamicGameObject {
    @JsonIgnore
    private static final Size size = new Size(38, 38);
    @JsonIgnore
    public final static String TYPE = "Fire";

    /**
     * The bomb that triggered this fire
     */
    private final Bomb bomb;
    /**
     * Timer in frames
     */
    @JsonIgnore
    private int timer = 0;

    /**
     * Max timer value in seconds
     */
    @JsonIgnore
    private float timerMax = 0.3f;

    public Fire(GameEntityRepository gameEntityRepository, Bomb bomb) {
        super(gameEntityRepository, size, TYPE);
        this.bomb = bomb;
    }

    @Override
    public void update() {
        if (bomb.isExploded()) {
            this.remove();
        }
        /*this.timer++;
        if (this.timer > this.timerMax * Ticker.FPS) {

        }**/
    }

    public void remove() {
        if (this.bomb.explodeListener != null) {
            this.bomb.explodeListener.apply(null);
            gameEntityRepository.removeBomb(this.bomb);
            this.bomb.explodeListener = null;
        }

        //gGameEngine.stage.removeChild(this.bmp);




        //this.bomb.getFires().remove(this);
        gameEntityRepository.removeFire(this);

        //gameEntityRepository.removeBomb(this.bomb);
    }

    public Bomb getBomb() {
        return bomb;
    }
}
