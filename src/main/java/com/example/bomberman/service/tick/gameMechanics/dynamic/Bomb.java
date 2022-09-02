package com.example.bomberman.service.tick.gameMechanics.dynamic;

import com.example.bomberman.service.tick.Ticker;
import com.example.bomberman.service.tick.gameMechanics.FinalPositionGameEntity;
import com.example.bomberman.service.tick.gameMechanics.Size;
import com.example.bomberman.service.tick.gameMechanics.Vector2;
import com.example.bomberman.service.tick.gameMechanics.dynamic.pawn.Pawn;
import com.example.bomberman.service.tick.gameMechanics.staticObj.tile.Material;
import com.example.bomberman.repos.GameEntityRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


public class Bomb extends FinalPositionGameEntity {

    /**
     * How far the fire reaches when bomb explodes
     */
    @JsonIgnore
    private int strength = 1;

    /**
     * Timer in frames
     */
    @JsonIgnore
    private int timer = 0;

    /**
     * Max timer value in seconds
     */
    @JsonIgnore
    private int timerMax = 2;
    @JsonIgnore
    private boolean exploded = false;
    @JsonIgnore
    private List<Fire> fires = new ArrayList<>();
    @JsonIgnore
    Function<Void, Void> explodeListener = null;

    @JsonIgnore
    public final static String TYPE = "Bomb";

    /**
     * Bitmap dimensions
     */
    @JsonIgnore
    private static final Size size = new Size(28, 28);

    @JsonIgnore
    Pawn pawn = null;

    public Bomb(GameEntityRepository gameEntityRepository, int strength) {
        super(gameEntityRepository, size, TYPE);
        this.strength = strength;


        // Allow players and bots that are already on this position to escape

        //gameEntityRepository.getPlayersAndBots(rect.getEntityPosition()).forEach(e -> e.setEscapeBomb(this));

        /*List<Pawn> players = gameObjectRepository.getPlayersAndBots();
        for (Pawn player : players) {
            if (player.comparePositions(this)) {
                player.setEscapeBomb(this);
            }
        }*/
    }

    @Override
    public void init() {

    }

    @Override
    public void update() {
        if (this.exploded) { return; }

        this.timer++;
        if (this.timer > this.timerMax * Ticker.FPS) {
            this.explode();
        }
    }

    public void explode() {
        this.exploded = true;

        // Fire in all directions!
        for (Fire fire : fires) {
            Material material = gameEntityRepository.getTileMaterial(fire.getEntityPosition());
            if (material == Material.WOOD) {
                gameEntityRepository.getTile(fire.getEntityPosition()).remove();
            } else if (material == Material.GRASS) {
                // Explode bombs in fire
                gameEntityRepository.getBombs(fire.getEntityPosition()).forEach(e -> { if (!e.exploded) e.explode(); });
            }
        }
    }

    /**
     * Returns positions that are going to be covered by fire.
     */
    public List<Vector2> getDangerPositions() {
        List<Vector2> positions = new ArrayList<>();
        positions.add(getEntityPosition());

        for (int i = 0; i < 4; i++) {

            Vector2 dir = getDirection(i);

            for (int j = 1; j <= this.strength; j++) {

                boolean explode = true;
                boolean last = false;

                Vector2 position = new Vector2( getEntityPosition().getX() + j * dir.getX(), getEntityPosition().getY() + j * dir.getY());

                Material material = gameEntityRepository.getTileMaterial(position);
                if (material == Material.WALL) { // One can not simply burn the wall
                    explode = false;
                    last = true;
                } else if (material == Material.WOOD) {
                    explode = true;
                    last = true;
                }

                if (explode) {
                    positions.add(position);
                }

                if (last) {
                    break;
                }
            }
        }

        return positions;
    }

    public void fire(Vector2 position) {
        Fire fire = new Fire(gameEntityRepository, this);
        fire.setEntityPosition(position);
        this.fires.add(fire);
        gameEntityRepository.addFire(fire);
    }

    public void remove() {
        gameEntityRepository.removeBomb(this);
    }

    public void setExplodeListener(Function<Void, Void> listener) {
        this.explodeListener = listener;
    }

    public List<Fire> getFires() {
        return fires;
    }

    public boolean isExploded() {
        return exploded;
    }

    @Override
    public void setEntityPosition(Vector2 entityPosition) {
        super.setEntityPosition(entityPosition);

        List<Vector2> positions = getDangerPositions();
        for (Vector2 position : positions) {
            fire(position);
        }
    }
}
