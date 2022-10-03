package com.example.bomberman.service.impl;

import com.example.bomberman.model.ExchangerGameId;
import com.example.bomberman.repos.ConnectionRepository;
import com.example.bomberman.service.ConnectionProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.Exchanger;

@Service
public class ConnectionProducerImpl implements ConnectionProducer {
    private static final Logger log = LoggerFactory.getLogger(ConnectionProducer.class);

    private final ConnectionRepository connectionRepository;

    public ConnectionProducerImpl(ConnectionRepository connectionRepository) {
        this.connectionRepository = connectionRepository;
    }

    public long produce(String name) {

        ExchangerGameId exchangerGameId = new ExchangerGameId(new Exchanger<>(), name);

        while (true) {
            try {
                if (connectionRepository.offerNewConnection(exchangerGameId, 5)) {
                    break;
                }
            } catch (InterruptedException e) {
                log.warn("Interrupted");
                return -1L;
            }
        }
        log.info("Connection {} added.", name);

        log.debug("Lock {}", exchangerGameId.namePlayer());

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
