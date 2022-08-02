package com.example.bomberman.model;

public class Connection {
    volatile private long playerId;
    private final String name;

    public Connection(long playerId, String name) {
        this.playerId = playerId;
        this.name = name;
    }

    public long getPlayerId() {
        return playerId;
    }

    public String getName() {
        return name;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    @Override
    public String toString() {
        return "Connection{" +
                "playerId=" + playerId +
                ", name='" + name + '\'' +
                '}';
    }
}
