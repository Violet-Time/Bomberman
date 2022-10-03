package com.example.bomberman.service.impl;

import com.example.bomberman.service.GameService;
import com.example.bomberman.service.GameSession;
import com.example.bomberman.service.game.Replicator;
import com.example.bomberman.service.game.core.logic.entity.pawn.Player;
import com.example.bomberman.service.network.Broker;
import com.example.bomberman.service.network.ConnectionPool;
import com.example.bomberman.service.game.core.InputEngine;
import com.example.bomberman.service.game.core.Ticker;
import com.example.bomberman.service.game.core.logic.GameMechanics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;

public class GameSessionImpl implements GameSession {
    private static final Logger log = LoggerFactory.getLogger(GameSession.class);
    public static final int MAX_PLAYERS_IN_GAME = 4;
    private final long id = GameService.generateId();
    private final ConnectionPool sessionConnectionPool;
    private final GameMechanics gameMechanics;
    private final InputEngine inputEngine;
    private final Replicator replicator;
    private final Broker broker;
    private final Thread ticker;


    public GameSessionImpl() {
        this.sessionConnectionPool = new ConnectionPool();
        this.inputEngine = new InputEngine();
        this.broker = new Broker(sessionConnectionPool, inputEngine);
        this.replicator = new Replicator(broker);
        this.gameMechanics = new GameMechanics(inputEngine, replicator);

        Ticker t = new Ticker();
        t.registerTicking(gameMechanics);
        t.registerTicking(replicator);

        this.ticker = new Thread(t,"GameSession " + id);
        log.debug("Create Game Session {}", id);
    }

    @Override
    public boolean isFullPlayers() {
        return replicator.playersCount() == MAX_PLAYERS_IN_GAME;
    }

    @Override
    public boolean addPlayer(String namePlayer) {
        if (replicator.playersCount() <= MAX_PLAYERS_IN_GAME) {
            log.debug("Add player {}", namePlayer);
            inputEngine.addPlayer(namePlayer);
            return replicator.addPlayer(new Player(inputEngine.getAction(namePlayer), replicator, namePlayer));
        }
        log.debug("Add player fail {} playersCount {}  MAX_PLAYERS_IN_GAME {} ", namePlayer, replicator.playersCount(), MAX_PLAYERS_IN_GAME);
        return false;
    }

    @Override
    public boolean addSocket(WebSocketSession session, String namePlayer) {
        if (replicator.getPlayer(namePlayer) != null) {
            sessionConnectionPool.add(session, namePlayer);
            log.info("Added Session {}", namePlayer);
            return true;
        } else {
            log.warn("Player {} not found", namePlayer);
            return false;
        }
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void start() {
        List<Player> lostPlayers = new ArrayList<>();
        for (Player player : replicator.getAllPlayers()) {
            if (!checkSocket(player.getName())) {
                lostPlayers.add(player);
                log.warn("Player {} lost", player.getName());
            }
        }
        lostPlayers.forEach(replicator::removePlayer);
        if (replicator.playersCount() == 0) {
            return;
        }
        replicator.posses();
        gameMechanics.createGame();
        ticker.start();
    }

    public boolean checkSocket(String namePlayer) {
        for (int i = 0; i < 5; i++) {
            WebSocketSession webSocketSession = sessionConnectionPool.getSession(namePlayer);
            if (webSocketSession == null) {
                log.warn("Socket not found! {}, attempt# {}", namePlayer, i);
            } else {
                log.info("Socket find " + namePlayer);
                return true;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    @Override
    public void connectionClose(WebSocketSession session) {
        String namePlayer = sessionConnectionPool.getPlayer(session);
        sessionConnectionPool.remove(session);
        inputEngine.removePlayer(namePlayer);
        Player player = replicator.getPlayer(namePlayer);
        if (player == null || replicator.removePlayer(player)) {
            log.info("Connection closed {}", namePlayer);
        }
    }

    @Override
    public Broker getBroker() {
        return broker;
    }

    @Override
    public int getPlayersInGame() {
        return replicator.playersCount();
    }

}
