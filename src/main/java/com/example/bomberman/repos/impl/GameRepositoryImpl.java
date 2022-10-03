package com.example.bomberman.repos.impl;

import com.example.bomberman.repos.GameRepository;
import com.example.bomberman.service.GameSession;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class GameRepositoryImpl implements GameRepository {
    private final ConcurrentHashMap<Long, GameSession> map = new ConcurrentHashMap<>();

    @Override
    public void put(GameSession session) {
        map.put(session.getId(), session);
    }

    @Override
    public GameSession getSession(Long gameId) {
        return map.get(gameId);
    }

    @Override
    public Collection<GameSession> getAll() {
        return map.values();
    }
}
