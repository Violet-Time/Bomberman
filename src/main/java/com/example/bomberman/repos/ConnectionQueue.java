package com.example.bomberman.repos;

import com.example.bomberman.model.ExchangerGameId;
import org.springframework.stereotype.Repository;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Repository
public class ConnectionQueue {
    private final BlockingQueue<ExchangerGameId> queue = new LinkedBlockingQueue<>();

    public BlockingQueue<ExchangerGameId> getQueue() {
        return queue;
    }
}
