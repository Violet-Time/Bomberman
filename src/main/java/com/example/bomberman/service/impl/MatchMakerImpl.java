package com.example.bomberman.service.impl;

import com.example.bomberman.model.ExchangerGameId;
import com.example.bomberman.repos.ConnectionRepository;
import com.example.bomberman.service.GameSession;
import com.example.bomberman.service.GameService;
import com.example.bomberman.service.MatchMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Service
public class MatchMakerImpl implements MatchMaker {
    private static final Logger log = LoggerFactory.getLogger(com.example.bomberman.service.MatchMaker.class);
    private static final int WAITING_TIME = 10;
    private final GameService gameService;
    private final ConnectionRepository connectionRepository;

    public MatchMakerImpl(GameService gameService, ConnectionRepository connectionRepository) {
        this.gameService = gameService;
        this.connectionRepository = connectionRepository;
    }

    @PostConstruct
    public void startThread() {
        new Thread(this, "match-maker").start();
    }

    @Override
    public void run() {
        log.info("Started");

        long start;
        GameSession gameSession = null;

        while (!Thread.currentThread().isInterrupted()) {
            start = System.currentTimeMillis();
            try {
                ExchangerGameId candidate = connectionRepository.pullNewConnection(
                        TimeUnit.SECONDS.toMillis(WAITING_TIME) - (System.currentTimeMillis() - start),
                        TimeUnit.MILLISECONDS);

                if (candidate != null) {

                    if (gameSession == null) {
                        gameSession = gameService.getGameSession(gameService.create());
                    }

                    if (gameSession.getPlayersInGame() == 0) {
                        start = System.currentTimeMillis();
                    }

                    gameService.connect(candidate.namePlayer(), gameSession.getId());
                    candidate.gameId().exchange(gameSession.getId());
                }

            } catch (InterruptedException e) {
                log.warn("Timeout reached");
            }

            //log.debug("waiting time {}", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - start));

            if (gameSession != null &&
                gameSession.getPlayersInGame() >= 1 &&
                    (gameSession.getPlayersInGame() == GameSessionImpl.MAX_PLAYERS_IN_GAME ||
                    System.currentTimeMillis() - start > TimeUnit.SECONDS.toMillis(WAITING_TIME))) {
                gameService.start(gameSession.getId());
                gameSession = null;
            }
        }

        log.info("Stop");
    }
}