package com.example.bomberman.service;

import com.example.bomberman.model.GameSession;

public interface GameService {
    long create();
    void start(long gameId);
    void connect(String nameOfPlayer, long gameId);

    GameSession getGameSession(long gameId);
}
