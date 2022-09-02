package com.example.bomberman.repos;

import com.example.bomberman.model.GameIdName;
import org.springframework.stereotype.Repository;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ConnectionRepository {
    private final ConcurrentHashMap<WebSocketSession, GameIdName> map = new ConcurrentHashMap<>();

    public void put(WebSocketSession webSocketSession, GameIdName connection) {
        map.put(webSocketSession, connection);
    }

    public GameIdName getConnection(WebSocketSession webSocketSession) {
        return map.get(webSocketSession);
    }
}
