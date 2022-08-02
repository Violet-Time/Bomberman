package com.example.bomberman.repos;

import com.example.bomberman.model.GameSession;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class GameRepositoryImpl implements GameRepository {
    private ConcurrentHashMap<Long, GameSession> map = new ConcurrentHashMap<>();

    public void put(GameSession session) {
        map.put(session.getId(), session);
    }

    public GameSession getSession(Long gameId) {
        return map.get(gameId);
    }

    public Collection<GameSession> getAll() {
        return map.values();
    }
}
