package com.example.bomberman.service.tick;

import com.example.bomberman.model.event.EventData;
import com.example.bomberman.model.Action;
import com.example.bomberman.model.Move;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InputEngine {
    Map<String, Action> actions = new ConcurrentHashMap<>();

    public void addPlayer(String name) {
        actions.put(name, new Action());
    }

    public void addAction(EventData eventData) {
        if (eventData != null) {
            switch (eventData.getTopic()) {
                case MOVE -> move(eventData);
                case PLANT_BOMB -> plantBomb(eventData);
                case JUMP -> jump(eventData);
            }
        }
    }

    private void plantBomb(EventData eventData) {
        Action action = actions.get(eventData.getNamePlayer());
        action.plantBomb();
    }

    private void jump(EventData eventData) {
        Action action = actions.get(eventData.getNamePlayer());
        action.jump();
    }
    private void move(EventData eventData) {
        Action action = actions.get(eventData.getNamePlayer());
        action.setMove(Move.valueOf(eventData.getData().get("direction").asText()));
    }

    public Action getAction(String name) {
        return actions.get(name);
    }
}
