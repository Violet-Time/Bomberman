package com.example.bomberman.repos.impl;

import com.example.bomberman.service.game.core.logic.entity.GameEntity;
import com.example.bomberman.service.game.core.logic.Vector2;
import com.example.bomberman.service.game.core.logic.entity.Bomb;
import com.example.bomberman.service.game.core.logic.entity.Fire;
import com.example.bomberman.service.game.core.logic.entity.pawn.Bot;
import com.example.bomberman.service.game.core.logic.entity.pawn.Pawn;
import com.example.bomberman.service.game.core.logic.entity.pawn.Player;
import com.example.bomberman.service.game.core.logic.entity.bonus.Bonus;
import com.example.bomberman.repos.GameEntityRepository;
import com.example.bomberman.service.game.core.logic.entity.tile.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class GameEntityRepositoryImpl implements GameEntityRepository {

    private final AtomicLong idGenerator = new AtomicLong();
    static class Cell {
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

        @Override
        public String toString() {
            return "Cell{" +
                    "bots=" + bots +
                    ", tiles=" + tiles +
                    ", bombs=" + bombs +
                    ", bonuses=" + bonuses +
                    ", fires=" + fires +
                    ", players=" + players +
                    '}';
        }
    }

    private final Map<Vector2, Cell> gameEntityGrid;

    private final Logger log = LoggerFactory.getLogger(GameEntityRepositoryImpl.class);

    public GameEntityRepositoryImpl() {

        this.gameEntityGrid = new HashMap<>();
    }

    private Cell getCell(Vector2 vector2) {
        return gameEntityGrid.computeIfAbsent(vector2, k -> new Cell());
    }

    @Override
    public boolean addPlayer(Player player) {
        Cell cell = getCell(player.getEntityPosition());
        cell.players.add(player);
        log.debug("Add player\n" + player);
        return true;
    }

    @Override
    public boolean addBot(Bot bot) {
        log.debug("Add bot {}", bot);
        getCell(bot.getEntityPosition()).bots.add(bot);
        return true;
    }

    @Override
    public boolean addTile(Tile tile) {
        getCell(tile.getEntityPosition()).tiles.add(tile);
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
        return getAllPlayers().stream()
                .filter(e -> Objects.equals(e.getName(), namePlayer))
                .findFirst().orElse(null);
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
    }

    @Override
    public List<Player> getAllPlayers() {
        List<Player> players = new ArrayList<>();
        gameEntityGrid.values().forEach(e -> players.addAll(e.players));
        return players;
    }

    @Override
    public List<Bot> getAllBots() {
        List<Bot> bots = new ArrayList<>();
        gameEntityGrid.values().forEach(e -> bots.addAll(e.bots));
        return bots;
    }

    @Override
    public List<Tile> getAllTitles() {
        List<Tile> tiles = new ArrayList<>();
        gameEntityGrid.values().forEach(e -> tiles.addAll(e.tiles));
        return tiles;
    }

    @Override
    public List<Bomb> getAllBombs() {
        List<Bomb> bombs = new ArrayList<>();
        gameEntityGrid.values().forEach(e -> bombs.addAll(e.bombs));
        return bombs;
    }

    @Override
    public List<Bonus> getAllBonuses() {
        List<Bonus> bonuses = new ArrayList<>();
        gameEntityGrid.values().forEach(e -> bonuses.addAll(e.bonuses));
        return bonuses;
    }

    @Override
    public List<Fire> getAllFires() {
        List<Fire> fires = new ArrayList<>();
        gameEntityGrid.values().forEach(e -> fires.addAll(e.fires));
        return fires;
    }

    @Override
    public List<GameEntity> getAllGameEntities() {
        List<GameEntity> gameEntities = new ArrayList<>();
        gameEntities.addAll(getAllPlayers());
        gameEntities.addAll(getAllBots());
        gameEntities.addAll(getAllTitles());
        gameEntities.addAll(getAllBombs());
        gameEntities.addAll(getAllBonuses());
        gameEntities.addAll(getAllFires());
        return gameEntities;
    }

    @Override
    public List<GameEntity> getGameEntitiesOnMapGrid(Vector2 vector2) {
        Cell cell = getCell(vector2);
        List<GameEntity> gameEntities = new ArrayList<>();
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
        List<Pawn> pawns = new ArrayList<>();
        pawns.addAll(getAllPlayers());
        pawns.addAll(getAllBots());
        return pawns;
    }

    @Override
    public List<Pawn> getPlayersAndBots(Vector2 vector2) {
        List<Pawn> pawns = new ArrayList<>();
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
    public boolean removeGameEntity(GameEntity gameEntity) {
        if (gameEntity instanceof Bot) {
            removeBot((Bot) gameEntity);
            return true;
        }

        if (gameEntity instanceof Player) {
            removePlayer((Player) gameEntity);
            return true;
        }

        if (gameEntity instanceof Tile) {
            removeTile((Tile) gameEntity);
            return true;
        }

        if (gameEntity instanceof Bomb) {
            removeBomb((Bomb) gameEntity);
            return true;
        }

        if (gameEntity instanceof Bonus) {
            removeBonus((Bonus) gameEntity);
            return true;
        }

        if (gameEntity instanceof Fire) {
            removeFire((Fire) gameEntity);
            return true;
        }

        return false;
    }

    @Override
    public boolean removePlayer(Player player) {
        if (getCell(player.getEntityPosition()).players.remove(player)) {
            log.debug("Remove player\n" + player);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeBot(Bot bot) {
        if (getCell(bot.getEntityPosition()).bots.remove(bot)) {
            log.debug("Remove bot\n" + bot);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeTile(Tile tile) {
        if (getCell(tile.getEntityPosition()).tiles.remove(tile)) {
            log.debug("Remove tile\n" + tile);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeBomb(Bomb bomb) {
        if (getCell(bomb.getEntityPosition()).bombs.remove(bomb)) {
            log.debug("Remove bomb\n" + bomb);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeBonus(Bonus bonus) {
        if (getCell(bonus.getEntityPosition()).bonuses.remove(bonus)) {
            log.debug("Remove bonus\n" + bonus);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeFire(Fire fire) {
        if (getCell(fire.getEntityPosition()).fires.remove(fire)) {
            log.debug("Remove fire\n" + fire);
            return true;
        }
        return false;
    }

    @Override
    public int playersCount() {
        AtomicInteger count = new AtomicInteger();
        gameEntityGrid.values().forEach(e -> count.addAndGet(e.players.size()));
        return count.get();
    }

    @Override
    public long generateId() {
        return idGenerator.getAndIncrement();
    }
}
