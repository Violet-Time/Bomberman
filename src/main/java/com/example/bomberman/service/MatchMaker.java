package com.example.bomberman.service;

import com.example.bomberman.model.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Service
public class MatchMaker implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(MatchMaker.class);
    private long currGameId = -1;
    private GameService gameService;
    private ConnectionQueue connectionQueue;

    public MatchMaker(GameService gameService, ConnectionQueue connectionQueue) {
        this.gameService = gameService;
        this.connectionQueue = connectionQueue;
    }

    @PostConstruct
    public void startThread() {
        new Thread(this, "match-maker").start();
    }

    @Override
    public void run() {
        log.info("Started");
        //List<Connection> candidates = new ArrayList<>(GameSession.PLAYERS_IN_GAME);
        while (!Thread.currentThread().isInterrupted()) {
            if (currGameId == -1) {
                currGameId = gameService.create();
            }

            try {
                Connection connection = connectionQueue.getQueue().poll(10_000, TimeUnit.SECONDS);
                log.info("Unlock {}", connection.getName());
                gameService.connect(connection.getName(), currGameId);
                connection.setPlayerId(currGameId);
            } catch (InterruptedException e) {
                log.warn("Timeout reached");
            }

            if (gameService.getGameSession(currGameId).isFullPlayers()) {
                gameService.start(currGameId);
                currGameId = -1;
                /*GameSession session = new GameSession(null);
                log.info(session.toString());
                gameRepository.put(session);
                candidates.clear();*/
            }
        }
    }

    //return gameId
    public long join(String player) {
        Connection connection = new Connection(-1, player);
        log.info("Lock {}", connection.getName());
        try {
            connectionQueue.getQueue().put(connection);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        log.info("Unlock join {}", connection.getName());

        while (connection.getPlayerId() == -1) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }

        return connection.getPlayerId();
    }
}
