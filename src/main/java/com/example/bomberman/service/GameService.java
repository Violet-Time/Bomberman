package com.example.bomberman.service;

import java.util.concurrent.atomic.AtomicLong;

public interface GameService {
    AtomicLong idGenerator = new AtomicLong();
    /**
     * Create game session
     * @return id of the created game session
     */
    long create();
    void start(long gameId);
    void connect(String nameOfPlayer, long gameId);

    GameSession getGameSession(long gameId);

    static long generateId() {
        return idGenerator.getAndIncrement();
    }
}
