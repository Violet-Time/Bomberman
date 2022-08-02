package com.example.bomberman.repos;

import com.example.bomberman.model.Move;
import com.example.bomberman.model.game.*;
import com.example.bomberman.model.game.dynamic.Bomb;
import com.example.bomberman.model.game.dynamic.DynamicGameObject;
import com.example.bomberman.model.game.dynamic.Fire;
import com.example.bomberman.model.game.dynamic.pawn.Player;
import com.example.bomberman.model.game.staticObj.StaticGameObject;
import com.example.bomberman.model.game.staticObj.bonus.Bonus;
import com.example.bomberman.model.game.dynamic.pawn.Bot;
import com.example.bomberman.model.game.dynamic.pawn.Pawn;
import com.example.bomberman.model.game.staticObj.tile.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class GameObjectRepositoryImpl  {

    /*private List<StaticGameObject> staticGameObjects;
    private List<DynamicGameObject> dynamicGameObjects;*/
    private final List<Bot> bots;
    private final List<Tile> tiles;
    private final List<Bomb> bombs;
    private final List<Bonus> bonuses;
    private final List<Fire> fires =  new ArrayList<>();
    private final Map<String, Pawn> players;

    private final List<Bot> deleteBots =  new ArrayList<>();
    private final List<Tile> deleteTiles =  new ArrayList<>();
    private final List<Bomb> deleteBombs =  new ArrayList<>();
    private final List<Bonus> deleteBonuses =  new ArrayList<>();
    private final List<Fire> deleteFires =  new ArrayList<>();

    private final List<Tile> newTiles =  new ArrayList<>();
    private final List<Bonus> newBonuses =  new ArrayList<>();
    private final List<Fire> newFires =  new ArrayList<>();

    private final List<Pawn> deletePlayers =  new ArrayList<>();

    private static AtomicLong gameObjId = new AtomicLong();

    public GameObjectRepositoryImpl() {
        this.bots = new ArrayList<>();
        this.tiles = new ArrayList<>();
        this.bombs = new ArrayList<>();
        this.bonuses = new ArrayList<>();
        players = new HashMap<>();
    }

    public Pawn getPlayer(String namePlayer) {
        return players.get(namePlayer);
    }

    public void addPlayer(Player player) {
        players.put(player.getName(), player);
    }

    public void addBot(Bot bot) {
        bots.add(bot);
    }

    public void addTile(Tile tile) {
        tiles.add(tile);
        newTiles.add(tile);
    }

    public void addBomb(Bomb bomb) {
        bombs.add(bomb);
    }

    public void addBonus(Bonus bonus) {
        bonuses.add(bonus);
        newBonuses.add(bonus);
    }

    public void addFire(Fire fire) {
        //fires.add(fire);
        newFires.add(fire);
    }

    public boolean addGameEntity(GameEntity gameEntity) {
        if (gameEntity instanceof Bot) {
            addBot((Bot) gameEntity);
            return true;
        }

        if (gameEntity instanceof Player) {
            addPlayer((Player) gameEntity);
            return true;
        }

        if (gameEntity instanceof Tile) {
            addTile((Tile) gameEntity);
            return true;
        }

        if (gameEntity instanceof Bomb) {
            addBomb((Bomb) gameEntity);
            return true;
        }

        if (gameEntity instanceof Bonus) {
            addBonus((Bonus) gameEntity);
            return true;
        }

        if (gameEntity instanceof Fire) {
            addFire((Fire) gameEntity);
            return true;
        }

        return false;
    }



    public List<Pawn> getPlayers() {
        return new ArrayList<>(players.values());
    }

    public List<Bot> getBots() {
        return bots;
    }

    public List<Tile> getTitles() {
        return tiles;
    }

    public List<Bomb> getBombs() {
        return bombs;
    }

    public List<Bonus> getBonuses() {
        return bonuses;
    }

    public List<Fire> getFires() {
        return fires;
    }

    /**
     * Returns tile at given position.
     */
    public Tile getTile(Vector2 position) {
        for (Tile tile : tiles) {
            if (tile.comparePositions(position)) {
                return tile;
            }
        }
        return null;
    }

    /**
     * Returns tile material at given position.
     */
    public Material getTileMaterial(Vector2 position) {
        Tile tile = getTile(position);

        if (tile == null || tile instanceof Grass) {
            return Material.GRASS;
        }

        if (tile instanceof Wall) {
            return Material.WALL;
        }

        if (tile instanceof Wood) {
            return Material.WOOD;
        }

        return Material.GRASS;
    }

    public Pawn getWinner() {
        for (Pawn player : players.values()) {
            if (player.isAlive()) {
                return player;
            }
        }
        return null;
    }

    public int countPlayersAlive() {
        int playersAlive = 0;
        for (Pawn player : players.values()) {
            if (player.isAlive()) {
                playersAlive++;
            }
        }
        return playersAlive;
    }

    public List<Pawn> getPlayersAndBots() {
        List<Pawn> players = new ArrayList<>();
        players.addAll(this.players.values());
        players.addAll(this.bots);

        return players;
    }

    public boolean removeBot(Bot bot) {
        return deleteBots.add(bot);
    }

    public boolean removeTile(Tile tile) {
        return deleteTiles.add(tile);
    }
    public boolean removeBomb(Bomb bomb) {
        return deleteBombs.add(bomb);
    }
    public boolean removeBonus(Bonus bonus) {
        return deleteBonuses.add(bonus);
    }

    public boolean removeFire(Fire fire) {
        return deleteFires.add(fire);
    }
    public static long getAndIncrementGameObjId() {
        return gameObjId.getAndIncrement();
    }

    public int playersCount() {
        return players.size();
    }

    public List<DynamicGameObject> getDynamicGameObject() {
        List<DynamicGameObject> dynamicGameObjects = new ArrayList<>();
        dynamicGameObjects.addAll(players.values());
        dynamicGameObjects.addAll(bots);
        dynamicGameObjects.addAll(bombs);
        return dynamicGameObjects;
    }

    public List<StaticGameObject> getStaticGameObjects() {
        List<StaticGameObject> staticGameObjects = new ArrayList<>();
        staticGameObjects.addAll(tiles);
        staticGameObjects.addAll(bonuses);
        return staticGameObjects;
    }

    public List<StaticGameObject> getRemovedStaticGameObjects() {

        List<StaticGameObject> staticGameObjects = new ArrayList<>();
        staticGameObjects.addAll(deleteTiles);
        staticGameObjects.addAll(deleteBonuses);
        return staticGameObjects;
    }

    public List<GameEntity> getGameEntityAndUpdate() {
        List<GameEntity> gameObjects = new ArrayList<>();

        bots.removeAll(deleteBots);
        deleteBots.clear();
        bombs.removeAll(deleteBombs);
        deleteBombs.clear();


        gameObjects.addAll(bots);
        gameObjects.addAll(bombs);
        gameObjects.addAll(players.values());

        gameObjects.addAll(newBonuses);
        gameObjects.addAll(deleteBonuses);

        gameObjects.addAll(newTiles);
        gameObjects.addAll(deleteTiles);

        gameObjects.addAll(newFires);

        //gameObjects.addAll(deleteFires);

        tiles.removeAll(deleteTiles);
        deleteTiles.clear();
        newTiles.clear();

        bonuses.removeAll(deleteBonuses);
        deleteBonuses.clear();
        newBonuses.clear();

        for (Fire fire : newFires) {
            fire.remove();
        }

        newFires.clear();
        //fires.removeAll(deleteFires);
        //deleteFires.clear();
        //newFires.clear();

        return gameObjects;
    }
    public Move getAction(long id) {
        return Move.UP;
    }

    public void update() {
        bots.removeAll(deleteBots);
        deleteBots.clear();
        /*tiles.removeAll(deleteTiles);
        deleteTiles.clear();*/
        bombs.removeAll(deleteBombs);
        deleteBombs.clear();
        bonuses.removeAll(deleteBonuses);
        deleteBonuses.clear();
    }

    //@Override
    public void resetDirection() {
        players.values().stream().map(e -> (Pawn) e).forEach(Pawn::resetDirection);
    }
}
