package com.example.bomberman.service;

import com.example.bomberman.model.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class ConnectionProducer {
    private static final Logger log = LoggerFactory.getLogger(ConnectionProducer.class);
    private static final String[] names = {"John", "Paul", "George", "Someone else"};

    private static AtomicLong id = new AtomicLong();

    private ConnectionQueue connectionQueue;

    public ConnectionProducer(ConnectionQueue connectionQueue) {
        this.connectionQueue = connectionQueue;
    }

    public long produce(String name) {
        long newId = id.getAndIncrement();

        while (!connectionQueue.getQueue().offer(new Connection(newId, name))) {
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                log.info("Interrupted");
            }
        }
        log.info("Connection {} added.", newId);
        return newId;
    }
}
