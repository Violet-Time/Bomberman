package com.example.bomberman.service.tick;

import com.example.bomberman.model.message.Message;
import com.example.bomberman.model.Action;
import com.example.bomberman.model.Move;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InputEngine implements Runnable {
    Map<String, Action> actions = new ConcurrentHashMap<>();
    InputQueue inputQueue;

    public InputEngine(InputQueue inputQueue) {
        this.inputQueue = inputQueue;
    }

    public void addPlayer(String name) {
        actions.put(name, new Action());
    }
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Message message = inputQueue.poll();
                if (message != null) {
                    switch (message.getTopic()) {
                        case MOVE -> move(message);
                        case PLANT_BOMB -> plantBomb(message);
                        case JUMP -> jump(message);
                    }
                }
            } catch (InterruptedException ignore) {
            }

        }

    }

    private void plantBomb(Message message) {
        Action action = actions.get(message.getNamePlayer());
        action.plantBomb();
    }

    private void jump(Message message) {
        Action action = actions.get(message.getNamePlayer());
        action.jump();
    }
    private void move(Message message) {
        Action action = actions.get(message.getNamePlayer());
        action.move(Move.valueOf(message.getData().get("direction").asText()));
    }

    public Action getAction(String name) {
        return actions.get(name);
    }

}
