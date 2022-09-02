package com.example.bomberman.model;

import com.example.bomberman.service.tick.gameMechanics.GameEntity;
import com.example.bomberman.service.tick.gameMechanics.dynamic.Fire;
import com.example.bomberman.service.tick.gameMechanics.dynamic.pawn.Bot;
import com.example.bomberman.service.tick.gameMechanics.dynamic.pawn.Player;
import com.example.bomberman.service.tick.gameMechanics.staticObj.bonus.Bonus;
import com.example.bomberman.service.tick.gameMechanics.staticObj.tile.Tile;
import com.example.bomberman.repos.impl.GameEntityRepositoryImpl;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Replica extends GameEntityRepositoryImpl {

    private final Queue<GameEntity> firstRead;

    private final Queue<Player> diePlayers;

    private int numberDeathsBots = 0;

    public Replica() {
        this.firstRead = new LinkedList<>();
        this.diePlayers = new LinkedList<>();
    }

    @Override
    public boolean addTile(Tile tile) {
        if (super.addTile(tile)) {
            return firstRead.add(tile);
        }
        return false;
    }

    @Override
    public boolean removePlayer(Player player) {
        if (super.removePlayer(player)) {
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
                numberDeathsBots++;
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

    public List<GameEntity> getGameEntitiesForSent() {
        List<GameEntity> gameEntities = new LinkedList<>();
        gameEntities.addAll(firstRead);
        gameEntities.addAll(getAllBombs());
        gameEntities.addAll(getPlayersAndBots());
        firstRead.clear();
        return gameEntities;
    }

    public Queue<Player> getDiePlayers() {
        return diePlayers;
    }

    public int getNumberDeathsBots() {
        return numberDeathsBots;
    }
}
