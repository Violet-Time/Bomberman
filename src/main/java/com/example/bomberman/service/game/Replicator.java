package com.example.bomberman.service.game;

import com.example.bomberman.repos.impl.GameEntityRepositoryImpl;
import com.example.bomberman.service.game.core.Ticking;
import com.example.bomberman.service.game.core.logic.entity.GameEntity;
import com.example.bomberman.service.game.core.logic.entity.Fire;
import com.example.bomberman.service.game.core.logic.entity.pawn.Bot;
import com.example.bomberman.service.game.core.logic.entity.pawn.Player;
import com.example.bomberman.service.game.core.logic.entity.bonus.Bonus;
import com.example.bomberman.service.game.core.logic.entity.tile.Tile;
import com.example.bomberman.service.network.Broker;

import java.util.*;

public class Replicator extends GameEntityRepositoryImpl implements Ticking {

    private final Map<String, Player> playersNameKey;

    private final Queue<GameEntity> firstRead;

    private final Queue<Player> diePlayers;

    private int numberAliveBots = 0;

    private final Broker broker;

    public Replicator(Broker broker) {
        this.broker = broker;
        this.playersNameKey = new HashMap<>();
        this.firstRead = new LinkedList<>();
        this.diePlayers = new LinkedList<>();
    }

    @Override
    public boolean addBot(Bot bot) {
        numberAliveBots++;
        return super.addBot(bot);
    }

    @Override
    public boolean addPlayer(Player player) {
        playersNameKey.put(player.getName(), player);
        if (player.getEntityPosition() == null) {
            return true;
        }
        return super.addPlayer(player);
    }

    @Override
    public boolean addTile(Tile tile) {
        if (super.addTile(tile)) {
            return firstRead.add(tile);
        }
        return false;
    }

    @Override
    public Player getPlayer(String namePlayer) {
        return playersNameKey.get(namePlayer);
    }

    @Override
    public boolean removePlayer(Player player) {

        if (player.getEntityPosition() == null) {
            playersNameKey.remove(player.getName());
            return true;
        }

        if (super.removePlayer(player)) {
            playersNameKey.remove(player.getName());
            if (!player.isAlive()) {
                diePlayers.add(player);
                firstRead.add(player);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean removeBot(Bot bot) {
        if (super.removeBot(bot)) {
            if (!bot.isAlive()) {
                numberAliveBots--;
                firstRead.add(bot);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean removeTile(Tile tile) {
        if (super.removeTile(tile)) {
            firstRead.add(tile);
            getBonuses(tile.getEntityPosition()).stream().findFirst().ifPresent(firstRead::add);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeBonus(Bonus bonus) {
        if (super.removeBonus(bonus)) {
            return firstRead.add(bonus);
        }
        return false;
    }

    @Override
    public boolean removeFire(Fire fire) {
        if (super.removeFire(fire)) {
            return firstRead.add(fire);
        }
        return false;
    }

    @Override
    public List<Player> getAllPlayers() {
        return playersNameKey.values().stream().toList();
    }

    public List<GameEntity> getGameEntitiesForSent() {
        List<GameEntity> gameEntities = new ArrayList<>();
        gameEntities.addAll(firstRead);
        gameEntities.addAll(getAllBombs());
        gameEntities.addAll(getPlayersAndBots());
        firstRead.clear();
        return gameEntities;
    }

    public Queue<Player> getDiePlayers() {
        return diePlayers;
    }

    public int playersCount() {
        return playersNameKey.size();
    }

    public void posses() {
        getAllPlayers().forEach(e -> broker.writePossess(e.getName(), e.getId()));
    }

    @Override
    public void tick(long elapsed) {
        broker.writeReplica(getGameEntitiesForSent());

        while (true) {
            Player player = getDiePlayers().poll();
            if (player != null) {
                broker.gameOver(player.getName(), "You lose:(");
            } else {
                break;
            }
        }

        if (getAllPlayers().size() == 1 && numberAliveBots == 0) {
            Player player = getAllPlayers().get(0);
            broker.gameOver(player.getName(), "You won:)");
        }

        if (getAllPlayers().size() < 1) {
            Thread.currentThread().interrupt();
        }
    }
}
