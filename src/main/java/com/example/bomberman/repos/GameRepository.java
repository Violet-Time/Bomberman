package com.example.bomberman.repos;

import com.example.bomberman.service.GameSession;

import java.util.Collection;

public interface GameRepository {
    void put(GameSession session);
    GameSession getSession(Long gameId);
    Collection<GameSession> getAll();
}
