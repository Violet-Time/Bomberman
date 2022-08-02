package com.example.bomberman.tick;

import com.example.bomberman.message.Message;
import lombok.EqualsAndHashCode;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@EqualsAndHashCode
public class InputQueue {
    private final ConcurrentLinkedQueue<Message> queue;

    public InputQueue() {
        queue = new ConcurrentLinkedQueue<>();
    }

    public void add(Message message) {
        queue.add(message);

    }

    public Message poll() {
        return queue.poll();
    }

}
