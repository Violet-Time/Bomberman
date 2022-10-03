package com.example.bomberman.service.impl;

import com.example.bomberman.service.GameSession;
import com.example.bomberman.repos.GameRepository;
import com.example.bomberman.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GameServiceImpl implements GameService {

    private static final Logger log = LoggerFactory.getLogger(GameService.class);
    private final GameRepository gameRepository;

    public GameServiceImpl(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public long create() {
        GameSession gameSession = new GameSessionImpl();
        gameRepository.put(gameSession);
        log.debug("Create {}", gameSession.getId());
        return gameSession.getId();
    }

    public void start(long gameId) {
        GameSession gameSession = gameRepository.getSession(gameId);
        if (gameSession == null) {
            log.error("Game session not found {}", gameId);
        } else {
            gameSession.start();
            log.info("Start {}", gameId);
        }

    }

    public void connect(String nameOfPlayer, long gameId) {
        if (!gameRepository.getSession(gameId).addPlayer(nameOfPlayer)) {
            log.warn("Connect {} fail", nameOfPlayer);
        }
    }

    public GameSession getGameSession(long gameId) {
        return gameRepository.getSession(gameId);
    }

}
