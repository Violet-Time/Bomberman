package com.example.bomberman.service.game.core;

import com.example.bomberman.model.event.EventData;
import com.example.bomberman.model.Action;
import com.example.bomberman.model.Move;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InputEngine {
    private final Map<String, Action> actions = new ConcurrentHashMap<>();

    public Action addPlayer(String namePlayer) {
        return actions.put(namePlayer, new Action());
    }

    public void removePlayer(String namePlayer) {
        actions.remove(namePlayer);
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
