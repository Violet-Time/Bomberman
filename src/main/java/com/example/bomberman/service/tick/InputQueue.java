package com.example.bomberman.service.tick;

import com.example.bomberman.model.message.Message;
import lombok.EqualsAndHashCode;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@EqualsAndHashCode
public class InputQueue {
    private final BlockingQueue<Message> queue;

    public InputQueue() {
        queue = new LinkedBlockingQueue<>();
    }

    public void add(Message message) {
        queue.add(message);

    }

    public Message poll() throws InterruptedException {
        return queue.poll(10_000, TimeUnit.SECONDS);
    }

}
