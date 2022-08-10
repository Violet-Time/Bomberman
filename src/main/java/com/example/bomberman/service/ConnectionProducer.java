package com.example.bomberman.service;

import com.example.bomberman.model.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ConnectionProducer {
    private static final Logger log = LoggerFactory.getLogger(ConnectionProducer.class);
    /*private static final String[] names = {"John", "Paul", "George", "Someone else"};

    private static AtomicLong id = new AtomicLong();*/

    private ConnectionQueue connectionQueue;

    public ConnectionProducer(ConnectionQueue connectionQueue) {
        this.connectionQueue = connectionQueue;
    }

    public long produce(String name) {

        Connection connection = new Connection(-1, name);

        while (!connectionQueue.getQueue().offer(connection)) {
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                log.info("Interrupted");
            }
        }
        log.info("Connection {} added.", name);

        log.info("Lock {}", connection.getName());
        /*try {
            connectionQueue.getQueue().put(connection);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/



        while (connection.getGameId() == -1) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }

        log.info("Unlock join {}", connection.getName());

        return connection.getGameId();
    }
}
