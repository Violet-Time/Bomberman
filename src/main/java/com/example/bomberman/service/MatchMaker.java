package com.example.bomberman.service;

import com.example.bomberman.model.ExchangerGameId;
import com.example.bomberman.model.GameSession;
import com.example.bomberman.repos.ConnectionQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Service
public class MatchMaker implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(MatchMaker.class);
    private static final int WAITING_TIME = 10;
    private final GameService gameService;
    private final ConnectionQueue connectionQueue;

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

        long start = 0;
        GameSession gameSession = null;

        while (!Thread.currentThread().isInterrupted()) {

            try {
                ExchangerGameId candidate = connectionQueue.getQueue().poll(WAITING_TIME, TimeUnit.SECONDS);

                if (candidate != null) {

                    if (gameSession == null) {
                        gameSession = gameService.getGameSession(gameService.create());
                        start = System.currentTimeMillis();
                    }

                    gameService.connect(candidate.name(), gameSession.getId());
                    candidate.gameId().exchange(gameSession.getId());
                }

            } catch (InterruptedException e) {
                log.warn("Timeout reached");
            }

            log.debug("waiting time {}", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start));

            if (gameSession != null &&
                    gameSession.getPlayersInGame() >= 1 &&
                        (gameSession.getPlayersInGame() == GameSession.MAX_PLAYERS_IN_GAME ||
                            TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start) > WAITING_TIME)) {
                gameService.start(gameSession.getId());
                gameSession = null;
            }
        }

        log.info("Stop");
    }
}
