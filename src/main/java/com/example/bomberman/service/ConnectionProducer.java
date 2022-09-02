package com.example.bomberman.service;

import com.example.bomberman.model.ExchangerGameId;
import com.example.bomberman.repos.ConnectionQueue;
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

        ExchangerGameId exchangerGameId = new ExchangerGameId(new Exchanger<>(), name);

        while (!connectionQueue.getQueue().offer(exchangerGameId)) {
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                log.warn("Interrupted");
            }
        }
        log.info("Connection {} added.", name);

        log.debug("Lock {}", exchangerGameId.name());

        long gameId = -1L;

        try {
            gameId = exchangerGameId.gameId().exchange(gameId);
        } catch (InterruptedException e) {
            log.warn("Interrupted");
            throw new RuntimeException(e);
        }

        log.debug("Unlock {} game id = {}", exchangerGameId.gameId(), gameId);

        return gameId;
    }
}
