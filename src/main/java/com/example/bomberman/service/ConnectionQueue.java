package com.example.bomberman.service;

import com.example.bomberman.model.Connection;
import org.springframework.stereotype.Repository;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

@Repository
public class ConnectionQueue {
    private BlockingQueue<Connection> queue = new LinkedBlockingQueue<>();

    public BlockingQueue<Connection> getQueue() {
        return queue;
    }
}
