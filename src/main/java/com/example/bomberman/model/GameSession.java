package com.example.bomberman.model;

import com.example.bomberman.network.ConnectionPool;
import com.example.bomberman.service.GameService;
import com.example.bomberman.tick.GameMechanics;
import com.example.bomberman.tick.InputQueue;
import com.example.bomberman.tick.Replicator;
import com.example.bomberman.tick.Ticker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;

public class GameSession {
    private static final Logger log = LoggerFactory.getLogger(GameSession.class);
    public static final int MAX_PLAYERS_IN_GAME = 4;

    private final long id = GameService.generateId();
    private final List<String> players;
    private ConnectionPool sessionConnectionPool;
    private GameMechanics gameMechanics;
    private InputQueue inputQueue;
    private Replicator replicator;
    private Thread ticker;

    public GameSession() {
        this.sessionConnectionPool = new ConnectionPool();
        this.players = new ArrayList<>();
        this.inputQueue = new InputQueue();
        this.replicator = new Replicator(sessionConnectionPool);
        this.gameMechanics = new GameMechanics(inputQueue, replicator);
        Ticker t = new Ticker();
        t.registerTickable(gameMechanics);
        this.ticker = new Thread(t);
        log.debug("Create Game Session {}", id);
    }

    /*public GameSession(int playersInGame) {
        this(new ArrayList<>(playersInGame), playersInGame);
    }*/

    public boolean isFullPlayers() {
        return players.size() == MAX_PLAYERS_IN_GAME;
    }

    public boolean addConnection(String namePlayer) {
        if (players.size() < MAX_PLAYERS_IN_GAME) {
            log.debug("Add connection {}", namePlayer);
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
                    log.warn("Socket not found! {}, attempt# {}", player, i);
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
        gameMechanics.createGame();
        ticker.start();
    }

    public void connectSessionPlayer(WebSocketSession session, String name) {
        if (players.contains(name)) {
            sessionConnectionPool.add(name, session);
            log.info("added Session {}", name);
        } else {
            log.warn("Player {} not found", name);
        }
    }

    public InputQueue getInputQueue() {
        return inputQueue;
    }

    public int getPlayersInGame() {
        return players.size();
    }

    @Override
    public String toString() {
        return "GameSession{" +
                "connections=" + players +
                ", id=" + id +
                '}';
    }
}
