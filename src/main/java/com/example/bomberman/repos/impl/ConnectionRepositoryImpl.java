package com.example.bomberman.repos.impl;

import com.example.bomberman.model.ExchangerGameId;
import com.example.bomberman.repos.ConnectionRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Repository
public class ConnectionRepositoryImpl implements ConnectionRepository {
    //current connections
    private final ConcurrentHashMap<WebSocketSession, Long> currConn = new ConcurrentHashMap<>();

    //queue of new connections waiting game id
    private final BlockingQueue<ExchangerGameId> queueOfNewConn = new LinkedBlockingQueue<>();

    @Override
    public void addConnection(WebSocketSession webSocketSession, Long gameId) {
        currConn.put(webSocketSession, gameId);
    }

    @Override
    public Long getConnection(WebSocketSession webSocketSession) {
        return currConn.get(webSocketSession);
    }

    @Override
    public void removeConnection(WebSocketSession webSocketSession) {
        currConn.remove(webSocketSession);
    }

    @Override
    public boolean offerNewConnection(ExchangerGameId exchangerGameId) {
        return queueOfNewConn.offer(exchangerGameId);
    }

    @Override
    public boolean offerNewConnection(ExchangerGameId exchangerGameId, long timeout) throws InterruptedException {
        return queueOfNewConn.offer(exchangerGameId, timeout, TimeUnit.SECONDS);
    }

    @Override
    public ExchangerGameId pullNewConnection(long time, TimeUnit timeUnit) throws InterruptedException {
        return queueOfNewConn.poll(time, timeUnit);
    }
}
