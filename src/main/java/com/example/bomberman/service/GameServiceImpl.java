package com.example.bomberman.service;

import com.example.bomberman.model.GameSession;
import com.example.bomberman.repos.GameRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GameServiceImpl implements GameService {

    private static final Logger log = LoggerFactory.getLogger(GameServiceImpl.class);
    private final GameRepositoryImpl gameRepository;
    //private long currGameId = -1;

    public GameServiceImpl(GameRepositoryImpl gameRepository) {
        this.gameRepository = gameRepository;
    }

    //Create game session
    //return id the game session
    public long create() {
        GameSession gameSession = new GameSession();
        gameRepository.put(gameSession);
        return gameSession.getId();
    }

    public void start(long gameId) {
        gameRepository.getSession(gameId).start();
        log.info("Start {}", gameId);
    }

    public void connect(String nameOfPlayer, long gameId) {
        gameRepository.getSession(gameId).addConnection(nameOfPlayer);
    }

    public GameSession getGameSession(long gameId) {
        return gameRepository.getSession(gameId);
    }
/*
    synchronized public long connect(String name) {
        if (currGameId == -1) {
            currGameId = create();
        }

        long gameId = currGameId;

        gameRepository.getSession(currGameId).addConnection(name);

        if (gameRepository.getSession(currGameId).isFullPlayers()) {
            start(currGameId);
            currGameId = -1;
        }

        return gameId;
    }*/
}
