package com.example.bomberman.model;

import com.example.bomberman.service.GameServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

public class Connection {
    private final AtomicLong gameId;
    private final String name;
    private static final Logger log = LoggerFactory.getLogger(Connection.class);

    public Connection(long gameId, String name) {
        this.gameId = new AtomicLong(gameId);
        this.name = name;
    }

    public long getGameId() {
        log.debug("get game id {}", gameId);
        return gameId.get();
    }

    public String getName() {
        return name;
    }

    public void setGameId(long gameId) {
        log.debug("set game id {}", gameId);
        this.gameId.set(gameId);
        log.debug("set game id {}", this.gameId.get());
    }

    @Override
    public String toString() {
        return "Connection{" +
                "playerId=" + gameId +
                ", name='" + name + '\'' +
                '}';
    }
}
