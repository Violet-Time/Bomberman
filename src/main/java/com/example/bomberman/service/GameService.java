package com.example.bomberman.service;

import com.example.bomberman.model.GameSession;

import java.util.concurrent.atomic.AtomicLong;

public interface GameService {
    AtomicLong idGenerator = new AtomicLong();
    long create();
    void start(long gameId);
    void connect(String nameOfPlayer, long gameId);

    GameSession getGameSession(long gameId);

    static long generateId() {
        return idGenerator.getAndIncrement();
    }
}
