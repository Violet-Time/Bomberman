package com.example.bomberman.controller.network;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConnectionPool {
    private static final Logger log = LoggerFactory.getLogger(ConnectionPool.class);
    public static final int PARALLELISM_LEVEL = 4;
    private final ConcurrentHashMap<String, WebSocketSession> pool;

    public ConnectionPool() {
        pool = new ConcurrentHashMap<>();
    }

    public void send(@NotNull WebSocketSession session, @NotNull String msg) {
        if (session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(msg));
            } catch (IOException ignored) {
            }
        }
    }

    public void broadcast(@NotNull String msg) {
        pool.forEachValue(PARALLELISM_LEVEL, session -> send(session, msg));
    }

    public void shutdown() {
        pool.forEachValue(PARALLELISM_LEVEL, session -> {
            if (session.isOpen()) {
                try {
                    session.close();
                } catch (IOException ignored) {
                }
            }
        });
    }

    public String getNamePlayer(WebSocketSession session) {
        return pool.entrySet().stream()
                .filter(entry -> entry.getValue().equals(session))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseGet(null);

    }

    public WebSocketSession getSession(String namePlayer) {
        return pool.get(namePlayer);
    }

    public void add(String namePlayer, WebSocketSession session) {
        if (pool.putIfAbsent(namePlayer, session) == null) {
            log.info("{} joined", namePlayer);
        }
    }

    public void remove(String namePlayer) {
        pool.remove(namePlayer);
    }

    public ConcurrentHashMap<String, WebSocketSession> getPool() {
        return pool;
    }
}
