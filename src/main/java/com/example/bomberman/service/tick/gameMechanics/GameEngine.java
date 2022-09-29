package com.example.bomberman.service.tick.gameMechanics;

import com.example.bomberman.service.tick.gameMechanics.dynamic.Fire;
import com.example.bomberman.service.tick.gameMechanics.staticObj.bonus.Bonus;
import com.example.bomberman.service.tick.gameMechanics.staticObj.bonus.BonusType;
import com.example.bomberman.service.tick.gameMechanics.dynamic.Bomb;
import com.example.bomberman.service.tick.gameMechanics.dynamic.pawn.Bot;
import com.example.bomberman.service.tick.gameMechanics.dynamic.pawn.Pawn;
import com.example.bomberman.service.tick.gameMechanics.dynamic.pawn.Player;
import com.example.bomberman.service.tick.gameMechanics.staticObj.tile.Tile;
import com.example.bomberman.service.tick.gameMechanics.staticObj.tile.Wall;
import com.example.bomberman.service.tick.gameMechanics.staticObj.tile.Wood;
import com.example.bomberman.repos.GameEntityRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameEngine {

    public static final int TILE_SIZE = 32;
    private static final int TILES_X = 27;
    private static final int TILES_Y = 17;
    //size: {},
    private int fps = 50;
    private int botsCount = 3; /* 0 - 3 */
    private int playersCount = 1; /* 1 - 2 */
    private final int bonusesPercent = 16;
    private final GameEntityRepository gameEntityRepository;

    @JsonIgnore
    private final Logger log = LoggerFactory.getLogger(GameEngine.class);

    public GameEngine(GameEntityRepository gameEntityRepository) {
        this.gameEntityRepository = gameEntityRepository;
        this.playersCount = gameEntityRepository.playersCount();
        this.botsCount = 4 - playersCount;
    }


    public void setup() {
        this.drawTiles();
        this.drawBonuses();

        this.spawnBots();
        this.spawnPlayers();
    }

    public void update(long elapsed) {

        // Bombs
        for (Bomb bomb : gameEntityRepository.getAllBombs()) {
            bomb.update(elapsed);
        }

        for (Pawn player : gameEntityRepository.getAllPlayers()) {
            player.update(elapsed);
        }

        // Bots
        for (Bot bot : gameEntityRepository.getAllBots()) {
            bot.update(elapsed);
        }

        for (Fire fire : gameEntityRepository.getAllFires()) {
            fire.update(elapsed);
        }
    }
    public void drawTiles() {
        for (int i = 0; i < this.TILES_Y; i++) {
            for (int j = 0; j < this.TILES_X; j++) {
                if ((i == 0 || j == 0 || i == this.TILES_Y - 1 || j == this.TILES_X - 1) ||
                        (j % 2 == 0 && i % 2 == 0)) {
                    // Wall tiles
                    Wall wall = new Wall(gameEntityRepository);
                    wall.setEntityPosition(new Vector2( j, i));
                    //gameEntityRepository.addTile(wall);

                } else {
                    // Grass tiles
                    //gameObjectRepository.addTile(new Grass(gameObjectRepository, new Point( j, i)));

                    // Wood tiles
                    if (!(i <= 2 && j <= 2)
                            && !(i >= this.TILES_Y - 3 && j >= this.TILES_X - 3)
                            && !(i <= 2 && j >= this.TILES_X - 3)
                            && !(i >= this.TILES_Y - 3 && j <= 2)) {

                        Wood wood = new Wood(gameEntityRepository);
                        wood.setEntityPosition(new Vector2( j, i));
                    }
                }
            }
        }
    }

    public void drawBonuses() {
        List<Wood> woods = new ArrayList<>();
        for (Tile tile : gameEntityRepository.getAllTitles()) {
            if (tile instanceof Wood) {
                woods.add((Wood) tile);
            }
        }

        // Sort tiles randomly
        Collections.shuffle(woods);

        // Distribute bonuses to quarters of map precisely fairly
        for (int j = 0; j < 4; j++) {
            long bonusesCount = Math.round(woods.size() * this.bonusesPercent * 0.01 / 4);
            int placedCount = 0;
            for (Wood wood : woods) {
                if (placedCount > bonusesCount) {
                    break;
                }

                if ((j == 0 && wood.getBitmapPosition().getX() < this.TILES_X / 2 && wood.getBitmapPosition().getY() < this.TILES_Y / 2)
                        || (j == 1 && wood.getBitmapPosition().getX() < this.TILES_X / 2 && wood.getBitmapPosition().getY() > this.TILES_Y / 2)
                        || (j == 2 && wood.getBitmapPosition().getX() > this.TILES_X / 2 && wood.getBitmapPosition().getY() < this.TILES_X / 2)
                        || (j == 3 && wood.getBitmapPosition().getX() > this.TILES_X / 2 && wood.getBitmapPosition().getY() > this.TILES_X / 2)) {

                    int typePosition = placedCount % 3;
                    Bonus bonus = new Bonus(gameEntityRepository, BonusType.values()[typePosition]);
                    bonus.setEntityPosition(wood.getEntityPosition());
                    placedCount++;
                }
            }
        }
    }

    public void spawnBots() {

        if (this.botsCount >= 1) {
            Bot bot = new Bot(gameEntityRepository, new Vector2(1, this.TILES_Y - 2));
        }

        if (this.botsCount >= 2) {
            Bot bot = new Bot(gameEntityRepository, new Vector2(this.TILES_X - 2, 1));
        }

        if (this.botsCount >= 3) {
            Bot bot = new Bot(gameEntityRepository, new Vector2(this.TILES_X - 2, this.TILES_Y - 2));
        }

        if (this.botsCount >= 4) {
            Bot bot = new Bot(gameEntityRepository, new Vector2(1, 1));
        }
    }

    public void spawnPlayers() {
        List<Player> players = gameEntityRepository.getAllPlayers();
        for (int i = 0; i < players.size(); i++) {
            if (i == 0) {
                players.get(i).setEntityPosition(new Vector2(1, 1));
            }

            if (i == 1) {
                players.get(i).setEntityPosition(new Vector2(this.TILES_X - 2, this.TILES_Y - 2));
            }

            if (i == 2) {
                players.get(i).setEntityPosition(new Vector2(this.TILES_X - 2, 1));
            }

            if (i == 3) {
                players.get(i).setEntityPosition(new Vector2(1, this.TILES_Y - 2));
            }
        }
    }

    public int getPlayersCount() {
        return playersCount;
    }

    public int getBotsCount() {
        return botsCount;
    }
}
