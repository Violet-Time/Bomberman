package com.example.bomberman.service.network;

import com.example.bomberman.service.game.core.InputEngine;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;
import com.example.bomberman.model.event.EventData;
import com.example.bomberman.model.event.Topic;
import com.example.bomberman.util.JsonHelper;

import java.io.IOException;

public class Broker {
    private static final Logger log = LoggerFactory.getLogger(Broker.class);
    private final ConnectionPool connectionPool;
    private final InputEngine inputEngine;

    public Broker(ConnectionPool connectionPool, InputEngine inputEngine) {
        this.connectionPool = connectionPool;
        this.inputEngine = inputEngine;
    }

    public void receive(@NotNull WebSocketSession session, @NotNull String message) {
        log.debug("RECEIVED: " + message);

        try {
            EventData eventData = JsonHelper.fromJson(message, EventData.class);
            eventData.setNamePlayer(connectionPool.getPlayer(session));
            if (eventData.getTopic() == Topic.MOVE
                    || eventData.getTopic() == Topic.PLANT_BOMB
                    || eventData.getTopic() == Topic.JUMP)
            {
                inputEngine.addAction(eventData);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void send(@NotNull String namePlayer, @NotNull Topic topic, @NotNull Object object) {
        WebSocketSession session = connectionPool.getSession(namePlayer);
        send(session, topic, object);
    }
    public void send(@NotNull WebSocketSession session, @NotNull Topic topic, @NotNull Object object) {
        String message = JsonHelper.toJson(new EventData(topic, JsonHelper.getJsonNode(object)));
        log.debug("SEND" + message);
        connectionPool.send(session, message);
    }

    public void broadcast(@NotNull Topic topic, @NotNull Object object) {
        String message = JsonHelper.toJson(new EventData(topic, JsonHelper.getJsonNode(object)));
        log.debug("SEND" + message);
        connectionPool.broadcast(message);
    }

    public void writeReplica(@NotNull Object object) {
        broadcast(Topic.REPLICA, object);
    }

    public void writePossess(@NotNull String player, long id) {
        send(player, Topic.POSSESS, id);
    }

    public void writePossess(@NotNull WebSocketSession session, long id) {
        send(session, Topic.POSSESS, id);
    }

    public void gameOver(@NotNull String namePlayer, @NotNull String msg) {
        WebSocketSession session = connectionPool.getSession(namePlayer);
        send(session, Topic.GAME_OVER, msg);
        try {
            session.close();
        } catch (IOException e) {
            log.warn("Session don`t close: {}", e.getMessage());
        }
    }

}
