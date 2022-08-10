package com.example.bomberman.repos.impl;

import com.example.bomberman.model.game.GameEntity;
import com.example.bomberman.model.game.Vector2;
import com.example.bomberman.model.game.dynamic.Bomb;
import com.example.bomberman.model.game.dynamic.Fire;
import com.example.bomberman.model.game.dynamic.pawn.Bot;
import com.example.bomberman.model.game.dynamic.pawn.Pawn;
import com.example.bomberman.model.game.dynamic.pawn.Player;
import com.example.bomberman.model.game.staticObj.bonus.Bonus;
import com.example.bomberman.model.game.staticObj.tile.*;
import com.example.bomberman.repos.GameEntityRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class GameEntityRepositoryImpl implements GameEntityRepository {

    private final AtomicLong idGenerator = new AtomicLong();
    class Cell {
        final Set<Bot> bots;
        final Set<Tile> tiles;
        final Set<Bomb> bombs;
        final Set<Bonus> bonuses;
        final Set<Fire> fires;
        final Set<Player> players;

        Cell() {
            this.bots = new HashSet<>();
            this.tiles = new HashSet<>();
            this.bombs = new HashSet<>();
            this.bonuses = new HashSet<>();
            this.fires = new HashSet<>();
            this.players = new HashSet<>();
        }
    }
    private final Map<String, Player> playersNameKey;
    private final Map<Vector2, Cell> gameEntityGrid;
    private final Queue<GameEntity> firstRead;

    @JsonIgnore
    private final Logger log = LoggerFactory.getLogger(GameEntityRepositoryImpl.class);

    public GameEntityRepositoryImpl() {
        this.playersNameKey = new HashMap<>();
        this.gameEntityGrid = new HashMap<>();
        this.firstRead = new LinkedList<>();
    }

    private Cell getCell(Vector2 vector2) {
        return gameEntityGrid.computeIfAbsent(vector2, k -> new Cell());
    }

    @Override
    public boolean addPlayer(Player player) {
        Cell cell = getCell(player.getEntityPosition());
        cell.players.add(player);
        playersNameKey.put(player.getName(), player);
        log.debug("Add player\n" + player);
        return true;
    }

    @Override
    public boolean addBot(Bot bot) {
        getCell(bot.getEntityPosition()).bots.add(bot);
        log.debug("Add bot\n" + bot);
        return true;
    }

    @Override
    public boolean addTile(Tile tile) {
        getCell(tile.getEntityPosition()).tiles.add(tile);
        firstRead.add(tile);
        log.debug("Add tile\n" + tile);
        return true;
    }

    @Override
    public boolean addBomb(Bomb bomb) {
        getCell(bomb.getEntityPosition()).bombs.add(bomb);
        log.debug("Add bomb\n" + bomb);
        return true;
    }

    @Override
    public boolean addBonus(Bonus bonus) {
        getCell(bonus.getEntityPosition()).bonuses.add(bonus);
        log.debug("Add bonus\n" + bonus);
        return true;
    }

    @Override
    public boolean addFire(Fire fire) {
        getCell(fire.getEntityPosition()).fires.add(fire);
        if (fire.getBomb().isExploded()) {
            firstRead.add(fire);
        }
        log.debug("Add fire\n" + fire);
        return true;
    }

    @Override
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

    @Override
    public Player getPlayer(String namePlayer) {
        return playersNameKey.get(namePlayer);
    }

    @Override
    public List<Player> getPlayers(Vector2 entityPosition) {
        return getCell(entityPosition).players.stream().toList();
    }

    @Override
    public List<Bot> getBots(Vector2 entityPosition) {
        return getCell(entityPosition).bots.stream().toList();
    }

    @Override
    public List<Bomb> getBombs(Vector2 entityPosition) {
        return getCell(entityPosition).bombs.stream().toList();
    }

    @Override
    public List<Bonus> getBonuses(Vector2 entityPosition) {
        return getCell(entityPosition).bonuses.stream().toList();
    }

    @Override
    public List<Fire> getFires(Vector2 entityPosition) {
        return getCell(entityPosition).fires.stream().toList();
    }

    @Override
    public Tile getTile(Vector2 entityPosition) {
        return getCell(entityPosition).tiles.stream().findFirst().orElse(null);

        /*List<GameEntity> gameEntities = getGameEntitiesOnMapGrid(position);
        if (gameEntities != null && !gameEntities.isEmpty()) {
            for (GameEntity gameEntity : gameEntities) {
                if (gameEntity instanceof Tile) {
                    return (Tile) gameEntity;
                }
            }
        }
        return null;*/
    }

    @Override
    public List<Player> getAllPlayers() {
        return new LinkedList<>(playersNameKey.values());
    }

    @Override
    public List<Bot> getAllBots() {
        List<Bot> bots = new LinkedList<>();
        gameEntityGrid.values().forEach(e -> bots.addAll(e.bots));
        return bots;
    }

    @Override
    public List<Tile> getAllTitles() {
        List<Tile> tiles = new LinkedList<>();
        gameEntityGrid.values().forEach(e -> tiles.addAll(e.tiles));
        return tiles;
    }

    @Override
    public List<Bomb> getAllBombs() {
        List<Bomb> bombs = new LinkedList<>();
        gameEntityGrid.values().forEach(e -> bombs.addAll(e.bombs));
        return bombs;
    }

    @Override
    public List<Bonus> getAllBonuses() {
        List<Bonus> bonuses = new LinkedList<>();
        gameEntityGrid.values().forEach(e -> bonuses.addAll(e.bonuses));
        return bonuses;
    }

    @Override
    public List<Fire> getAllFires() {
        List<Fire> fires = new LinkedList<>();
        gameEntityGrid.values().forEach(e -> fires.addAll(e.fires));
        return fires;
    }

    @Override
    public List<GameEntity> getAllGameEntities() {
        List<GameEntity> gameEntities = new LinkedList<>();
        gameEntities.addAll(playersNameKey.values());
        gameEntities.addAll(getAllBots());
        gameEntities.addAll(getAllTitles());
        gameEntities.addAll(getAllBombs());
        gameEntities.addAll(getAllBonuses());
        gameEntities.addAll(getAllFires());
        return gameEntities;
    }

    @Override
    public List<GameEntity> getGameEntitiesForSent() {
        List<GameEntity> gameEntities = new LinkedList<>();
        gameEntities.addAll(firstRead);
        gameEntities.addAll(getAllBombs());
        gameEntities.addAll(getAllBots());
        gameEntities.addAll(playersNameKey.values());
        firstRead.clear();

        return gameEntities;
    }

    @Override
    public List<GameEntity> getGameEntitiesOnMapGrid(Vector2 vector2) {
        Cell cell = getCell(vector2);
        List<GameEntity> gameEntities = new LinkedList<>();
        gameEntities.addAll(cell.bombs);
        gameEntities.addAll(cell.bonuses);
        gameEntities.addAll(cell.bots);
        gameEntities.addAll(cell.players);
        gameEntities.addAll(cell.fires);
        gameEntities.addAll(cell.tiles);
        return gameEntities;
    }

    @Override
    public List<Pawn> getPlayersAndBots() {
        List<Pawn> pawns = new LinkedList<>();
        pawns.addAll(this.playersNameKey.values());
        pawns.addAll(getAllBots());
        return pawns;
    }

    @Override
    public List<Pawn> getPlayersAndBots(Vector2 vector2) {
        List<Pawn> pawns = new LinkedList<>();
        Cell cell = getCell(vector2);
        pawns.addAll(cell.players);
        pawns.addAll(cell.bots);
        return pawns;
    }

    @Override
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

    @Override
    public boolean removePlayer(Player player) {
        firstRead.add(player);
        playersNameKey.remove(player.getName());
        log.debug("Remove player\n" + player);
        return getCell(player.getEntityPosition()).players.remove(player);
    }

    @Override
    public boolean removeBot(Bot bot) {
        firstRead.add(bot);
        log.debug("Remove bot\n" + bot);
        return getCell(bot.getEntityPosition()).bots.remove(bot);
    }

    @Override
    public boolean removeTile(Tile tile) {
        firstRead.add(tile);
        getBonuses(tile.getEntityPosition()).stream().findFirst().ifPresent(firstRead::add);
        log.debug("Remove tile\n" + tile);
        return getCell(tile.getEntityPosition()).tiles.remove(tile);
    }

    @Override
    public boolean removeBomb(Bomb bomb) {
        log.debug("Remove bomb\n" + bomb);
        return getCell(bomb.getEntityPosition()).bombs.remove(bomb);
    }

    @Override
    public boolean removeBonus(Bonus bonus) {
        firstRead.add(bonus);
        log.debug("Remove bonus\n" + bonus);
        return getCell(bonus.getEntityPosition()).bonuses.remove(bonus);
    }

    @Override
    public boolean removeFire(Fire fire) {
        firstRead.add(fire);
        log.debug("Remove fire\n" + fire);
        return getCell(fire.getEntityPosition()).fires.remove(fire);
    }

    @Override
    public int playersCount() {
        return playersNameKey.size();
    }

    @Override
    public long generateId() {
        return idGenerator.getAndIncrement();
    }
}
