package com.example.bomberman.tick;

import com.example.bomberman.message.Message;
import com.example.bomberman.message.Topic;
import com.example.bomberman.model.Action;
import com.example.bomberman.model.Move;
import com.example.bomberman.model.game.dynamic.pawn.Pawn;

import java.util.HashMap;
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
            Message message = inputQueue.poll();
            if (message != null) {
                switch (message.getTopic()) {
                    case MOVE -> move(message);
                    case PLANT_BOMB -> plantBomb(message);
                    case JUMP -> jump(message);
                }
            }
        }

    }

    private void plantBomb(Message message) {
        Action action = actions.get(message.getPlayerName());
        action.plantBomb();
    }

    private void jump(Message message) {
        Action action = actions.get(message.getPlayerName());
        action.jump();
    }
    private void move(Message message) {
        Action action = actions.get(message.getPlayerName());
        action.move(Move.valueOf(message.getData().get("direction").asText()));
    }

    public Action getAction(String name) {
        return actions.get(name);
    }

}
