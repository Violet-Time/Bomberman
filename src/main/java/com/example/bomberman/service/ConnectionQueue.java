package com.example.bomberman.service;

import com.example.bomberman.model.Connection;
import org.springframework.stereotype.Repository;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

@Repository
public class ConnectionQueue {
    private SynchronousQueue<Connection> queue = new SynchronousQueue<>();

    public SynchronousQueue<Connection> getQueue() {
        return queue;
    }
}
