package com.example.bomberman.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Exchanger;

public class Connection {
    private final Exchanger<Long> gameId;
    private final String name;

    private static final Logger log = LoggerFactory.getLogger(Connection.class);

    public Connection(Exchanger<Long> gameId, String name) {
        this.gameId = gameId;
        this.name = name;
    }

    public Exchanger<Long> getGameId() {
        return gameId;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Connection{" +
                "playerId=" + gameId +
                ", name='" + name + '\'' +
                '}';
    }
}
