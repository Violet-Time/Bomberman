package com.example.bomberman.repos;

import com.example.bomberman.model.ExchangerGameId;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.TimeUnit;

public interface ConnectionRepository {

    void addConnection(WebSocketSession webSocketSession, Long gameId);

    Long getConnection(WebSocketSession webSocketSession);

    void removeConnection(WebSocketSession webSocketSession);

    boolean offerNewConnection(ExchangerGameId exchangerGameId);
    boolean offerNewConnection(ExchangerGameId exchangerGameId, long timeout) throws InterruptedException ;

    ExchangerGameId pullNewConnection(long time, TimeUnit timeUnit) throws InterruptedException;
}
