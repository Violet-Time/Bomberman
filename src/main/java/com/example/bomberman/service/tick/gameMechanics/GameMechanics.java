package com.example.bomberman.service.tick.gameMechanics;

import com.example.bomberman.model.event.Topic;
import com.example.bomberman.model.Replica;
import com.example.bomberman.service.tick.Replicator;
import com.example.bomberman.service.tick.Ticking;
import com.example.bomberman.service.tick.gameMechanics.dynamic.pawn.Player;
import com.example.bomberman.controller.network.ConnectionPool;
import com.example.bomberman.service.tick.InputEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class GameMechanics implements Ticking {

    private final Replica gameEntityRepository;

    private final Replicator replicator;
    private final InputEngine inputEngine;

    private GameEngine gameEngine;

    private final Logger log = LoggerFactory.getLogger(GameMechanics.class);

    public GameMechanics(InputEngine inputEngine, Replicator replicator) {
        this.inputEngine = inputEngine;
        this.replicator = replicator;
        this.gameEntityRepository = new Replica();
    }

    public void createGame() {

        replicator.getConnectionPool().getPool().forEach(ConnectionPool.PARALLELISM_LEVEL,
                (name, webSocketSession) -> {
                    inputEngine.addPlayer(name);
                    Player player = new Player(inputEngine.getAction(name), gameEntityRepository, name);
                    gameEntityRepository.addPlayer(player);
                    replicator.writePossess(webSocketSession, player.getId());
        });

        this.gameEngine = new GameEngine(gameEntityRepository);
        this.gameEngine.setup();
    }

    @Override
    public void tick(long elapsed) {

        gameEngine.update(elapsed);

        replicator.broadcast(Topic.REPLICA, gameEntityRepository.getGameEntitiesForSent());

        while (true) {
            Player player = gameEntityRepository.getDiePlayers().poll();
            if (player != null) {
                replicator.gameOver(player.getName(), "You lose:(");
            } else {
                break;
            }
        }

        if (gameEntityRepository.getAllPlayers().size() == 1 && gameEngine.getBotsCount() == gameEntityRepository.getNumberDeathsBots()) {
            Player player = gameEntityRepository.getAllPlayers().get(0);
            replicator.gameOver(player.getName(), "You won:)");
        }

        if (gameEntityRepository.getAllPlayers().size() < 1) {
            Thread.currentThread().interrupt();
        }
    }

    /*private void doMechanics(Message message) {
        log.info(message.toString());
        switch (message.getTopic()) {
            case MOVE -> move(message);
            case PLANT_BOMB -> plantBomb(message);
            default -> {
                return;
            }
        }
    }*/

    /*private void plantBomb(Message message) {
        Pawn player = gameEntityRepository.getPlayer(message.getNamePlayer());
        player.setDirection(Move.PLANT_BOMB);
    }

    private void move(Message message) {
        Pawn player = gameEntityRepository.getPlayer(message.getNamePlayer());
        player.setDirection(Move.valueOf(message.getData().get("direction").asText()));
    }*/

    public void removePlayer(String name) {
        Player player = gameEntityRepository.getPlayer(name);
        if (player != null) {
            gameEntityRepository.removePlayer(player);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return (o == null || getClass() != o.getClass());

    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass());
    }
}
