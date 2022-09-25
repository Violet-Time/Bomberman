package com.example.bomberman.service;

import com.example.bomberman.model.GameSession;
import com.example.bomberman.repos.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GameServiceImpl implements GameService {

    private static final Logger log = LoggerFactory.getLogger(GameServiceImpl.class);
    private final GameRepository gameRepository;

    public GameServiceImpl(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    /**
     * Create game session
     * @return id of the created game session
     */
    public long create() {
        GameSession gameSession = new GameSession();
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
        gameRepository.getSession(gameId).addConnection(nameOfPlayer);
    }

    public GameSession getGameSession(long gameId) {
        return gameRepository.getSession(gameId);
    }

}
