package com.example.bomberman.model;

import com.example.bomberman.controller.network.ConnectionPool;
import com.example.bomberman.model.event.EventData;
import com.example.bomberman.service.GameService;
import com.example.bomberman.service.tick.InputEngine;
import com.example.bomberman.service.tick.gameMechanics.GameMechanics;
import com.example.bomberman.service.tick.Replicator;
import com.example.bomberman.service.tick.Ticker;
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
    private final ConnectionPool sessionConnectionPool;
    private final GameMechanics gameMechanics;
    private final InputEngine inputEngine;
    private final Replicator replicator;
    private final Thread ticker;

    public GameSession() {
        this.sessionConnectionPool = new ConnectionPool();
        this.players = new ArrayList<>();
        this.inputEngine = new InputEngine();
        this.replicator = new Replicator(sessionConnectionPool);
        this.gameMechanics = new GameMechanics(inputEngine, replicator);
        Ticker t = new Ticker();
        t.registerTicking(gameMechanics);
        this.ticker = new Thread(t,"GameSession " + id);
        log.debug("Create Game Session {}", id);
    }

    public boolean isFullPlayers() {
        return players.size() == MAX_PLAYERS_IN_GAME;
    }

    public boolean addConnection(String namePlayer) {
        if (players.size() <= MAX_PLAYERS_IN_GAME) {
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
            tryConnectWebSocketSession(player);
        }
        log.info("Start ticker");
        gameMechanics.createGame();
        ticker.start();
    }

    public void tryConnectWebSocketSession(String player) {
        for (int i = 0; i < 5; i++) {
            WebSocketSession webSocketSession = sessionConnectionPool.getSession(player);
            if (webSocketSession == null) {
                log.warn("Socket not found! {}, attempt# {}", player, i);
            } else {
                log.info("Socket find " + player);
                return;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        //TODO

    }

    public void connectSessionPlayer(WebSocketSession session, String name) {
        if (players.contains(name)) {
            sessionConnectionPool.add(name, session);
            log.info("added Session {}", name);
        } else {
            log.warn("Player {} not found", name);
        }
    }

    public void connectionClose(String name) {
        if (players.contains(name)) {
            sessionConnectionPool.remove(name);
            gameMechanics.removePlayer(name);
            players.remove(name);
            log.info("remove Session {}", name);
        } else {
            log.warn("Player {} not found", name);
        }
    }

    public void addInput(EventData eventData) {
        inputEngine.addAction(eventData);
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
