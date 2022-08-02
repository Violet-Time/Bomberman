package com.example.bomberman.model;

import com.example.bomberman.network.ConnectionPool;
import com.example.bomberman.tick.GameMechanics;
import com.example.bomberman.tick.InputQueue;
import com.example.bomberman.tick.Replicator;
import com.example.bomberman.tick.Ticker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

public class GameSession {
    private static final Logger log = LoggerFactory.getLogger(GameSession.class);
    public static final int PLAYERS_IN_GAME = 1;
    private static AtomicLong idGenerator = new AtomicLong();
    private final long id = idGenerator.getAndIncrement();
    private final List<String> players;
    private ConnectionPool sessionConnectionPool;
    private GameMechanics gameMechanics;
    private InputQueue inputQueue;
    private Replicator replicator;
    private Thread ticker;

    public GameSession(List<String> players) {
        this.sessionConnectionPool = new ConnectionPool();
        this.players = players;
        this.inputQueue = new InputQueue();
        this.replicator = new Replicator(sessionConnectionPool);
        this.gameMechanics = new GameMechanics(inputQueue, replicator);
        Ticker t = new Ticker();
        t.registerTickable(gameMechanics);
        this.ticker = new Thread(t);
    }

    public GameSession() {
        this(new ArrayList<>(PLAYERS_IN_GAME));
    }

    public boolean isFullPlayers() {
        return players.size() == PLAYERS_IN_GAME;
    }

    public boolean addConnection(String namePlayer) {
        if (players.size() < PLAYERS_IN_GAME) {
            return players.add(namePlayer);
        }
        return false;
    }

    public long getId() {
        return id;
    }

    public void start() {
        for (String player : players) {
            for (int i = 0; i < 5; i++) {
                WebSocketSession webSocketSession = sessionConnectionPool.getSession(player);
                if (webSocketSession == null) {
                    //throw new RuntimeException("Socket not found!");
                    log.error("Socket not found! " + player);
                } else {
                    log.info("Socket find " + player);
                    //sessionConnectionPool.add(player, webSocketSession);
                    break;
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        log.info("Start ticker");
        gameMechanics.generateGameObjects();
        ticker.start();
    }

    public void connectSessionPlayer(WebSocketSession session, String name) {
        if (players.contains(name)) {
            sessionConnectionPool.add(name, session);
            log.info("added Session " + name);
        } else {
            log.warn("Player {} not found", name);
        }
    }

    public InputQueue getInputQueue() {
        return inputQueue;
    }

    @Override
    public String toString() {
        return "GameSession{" +
                "connections=" + players +
                ", id=" + id +
                '}';
    }
}
