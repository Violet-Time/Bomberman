package com.example.bomberman.service;

import com.example.bomberman.model.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.Exchanger;

@Service
public class ConnectionProducer {
    private static final Logger log = LoggerFactory.getLogger(ConnectionProducer.class);

    private final ConnectionQueue connectionQueue;

    public ConnectionProducer(ConnectionQueue connectionQueue) {
        this.connectionQueue = connectionQueue;
    }

    public long produce(String name) {

        Connection connection = new Connection(new Exchanger<>(), name);

        while (!connectionQueue.getQueue().offer(connection)) {
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                log.warn("Interrupted");
            }
        }
        log.info("Connection {} added.", name);

        log.debug("Lock {}", connection.getName());

        long gameId = -1L;

        try {
            gameId = connection.getGameId().exchange(gameId);
        } catch (InterruptedException e) {
            log.warn("Interrupted");
            throw new RuntimeException(e);
        }

        log.debug("Unlock {} game id = {}", connection.getName(), gameId);

        return gameId;
    }
}
