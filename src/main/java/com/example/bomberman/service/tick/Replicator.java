package com.example.bomberman.service.tick;

import com.example.bomberman.model.event.EventData;
import com.example.bomberman.model.event.Topic;
import com.example.bomberman.controller.network.ConnectionPool;
import com.example.bomberman.util.JsonHelper;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@EqualsAndHashCode
public class Replicator {
    private final ConnectionPool connectionPool;

    private static final Logger log = LoggerFactory.getLogger(Replicator.class);
    public Replicator(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public void writeReplica(String player, Object object) {
        send(player, Topic.REPLICA, object);
    }

    public void writePossess(@NotNull String player, long id) {
        send(player, Topic.POSSESS, id);
    }

    public void writePossess(@NotNull WebSocketSession session, long id) {
        send(session, Topic.POSSESS, id);
    }

    public void receive(@NotNull WebSocketSession session, @NotNull String msg) {
        log.debug("RECEIVED: " + msg);
        EventData eventData = JsonHelper.fromJson(msg, EventData.class);
        //TODO TASK2 implement message processing
    }

    public void send(@NotNull String player, @NotNull Topic topic, @NotNull Object object) {
        WebSocketSession session = connectionPool.getSession(player);
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

    public void gameOver(@NotNull String player, @NotNull String msg) {
        WebSocketSession session = connectionPool.getSession(player);
        send(session, Topic.GAME_OVER, msg);
        try {
            session.close();
            //connectionPool.remove(player);
        } catch (IOException e) {
            log.warn("Session don`t close: {}", e.getMessage());
        }
    }

    public ConnectionPool getConnectionPool() {
        return connectionPool;
    }
}
