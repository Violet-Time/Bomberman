package com.example.bomberman.service;

import com.example.bomberman.model.Connection;
import com.example.bomberman.model.GameSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class MatchMaker implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(MatchMaker.class);
    private static final int WAITING_TIME = 30;
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
                Connection candidate = connectionQueue.getQueue().poll(10_000, TimeUnit.MILLISECONDS);

                if (candidate != null) {

                    if (gameSession == null) {
                        gameSession = gameService.getGameSession(gameService.create());
                        start = System.currentTimeMillis();
                    }

                    gameService.connect(candidate.getName(), gameSession.getId());
                    candidate.getGameId().exchange(gameSession.getId());
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
