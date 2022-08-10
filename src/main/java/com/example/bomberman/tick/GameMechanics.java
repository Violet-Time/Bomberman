package com.example.bomberman.tick;

import com.example.bomberman.message.Message;
import com.example.bomberman.message.Topic;
import com.example.bomberman.model.Move;
import com.example.bomberman.model.game.dynamic.pawn.Pawn;
import com.example.bomberman.model.game.dynamic.pawn.Player;
import com.example.bomberman.network.ConnectionPool;
import com.example.bomberman.repos.GameEntityRepository;
import com.example.bomberman.repos.impl.GameEntityRepositoryImpl;
import com.example.bomberman.tick.Game.GameEngine;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class GameMechanics implements Tickable {

    private GameEntityRepository gameEntityRepository;

    //private static AtomicLong gameObjId = new AtomicLong();
    //private long gameObjId = 1;

    private InputQueue inputQueue;
    private Replicator replicator;
    private InputEngine inputEngine;

    private GameEngine gameEngine;

    private final Logger log = LoggerFactory.getLogger(GameMechanics.class);

    public GameMechanics(InputQueue inputQueue, Replicator replicator) {
        this.inputQueue = inputQueue;
        this.replicator = replicator;
        this.gameEntityRepository = new GameEntityRepositoryImpl();
        this.inputEngine = new InputEngine(inputQueue);
    }

    public void createGame() {

        replicator.getConnectionPool().getPool().forEach(ConnectionPool.PARALLELISM_LEVEL,
                (name, webSocketSession) -> {
                    inputEngine.addPlayer(name);
                    Player player = new Player(inputEngine.getAction(name), gameEntityRepository, name);
                    gameEntityRepository.addPlayer(player);
                    replicator.writePossess(webSocketSession, player.getId());
                    //gameObjId++;
        });

        this.gameEngine = new GameEngine(gameEntityRepository);
        this.gameEngine.setup();

        new Thread(inputEngine).start();

        /*for (int i = 2; i < 15; i += 2) {
            for (int j = 2; j < 15; j += 2) {
                gameObjects.add(new Wall(gameObjId.getAndIncrement(), new Point(i, j)));
            }
        }*/
        /*gameObjectRepository.add(new Wall(gameObjId.getAndIncrement(), new Point(10, 10)));
        gameObjectRepository.add(new Wall(gameObjId.getAndIncrement(), new Point(100, 10)));
        gameObjectRepository.add(new Wall(gameObjId.getAndIncrement(), new Point(10, 100)));*/
        //replicator.broadcast(Topic.REPLICA, gameObjectRepository.getStaticGameObjects());

    }
    public void spawnPlayers() {


    }

    @Override
    public void tick(long elapsed) {

        gameEngine.update();

        replicator.broadcast(Topic.REPLICA, gameEntityRepository.getGameEntitiesForSent());
        //gameObjectRepository.resetDirection();
    }

    private void doMechanics(Message message) {
        log.info(message.toString());
        switch (message.getTopic()) {
            case MOVE -> move(message);
            case PLANT_BOMB -> plantBomb(message);
            default -> {
                return;
            }
        }
    }

    private void plantBomb(Message message) {
        Pawn player = gameEntityRepository.getPlayer(message.getPlayerName());
        //log.info(message.getData().get("direction").asText());
        player.setDirection(Move.PLANT_BOMB);
        /*Bomb bomb = new Bomb(gameObjId.getAndIncrement(), new Point(gameObjectRepository.getPlayer(message.getPlayerName()).getPosition()));
        gameObjectRepository.add(bomb);*/
        //replicator.broadcast(Topic.REPLICA, bomb);
    }

    private void move(Message message) {
        Pawn player = gameEntityRepository.getPlayer(message.getPlayerName());
        log.info(message.getData().get("direction").asText());
        player.setDirection(Move.valueOf(message.getData().get("direction").asText()));
        /*switch () {
            case UP -> player.up();
            case DOWN -> player.down();
            case LEFT -> player.left();
            case RIGHT -> player.right();
        }
        replicator.broadcast(Topic.REPLICA, player);*/
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

    @Override
    public int compareTo(@NotNull Object o) {
        return 0;
    }
}
