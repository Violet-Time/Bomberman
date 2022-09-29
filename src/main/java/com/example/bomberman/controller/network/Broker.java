package com.example.bomberman.controller.network;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;
import com.example.bomberman.model.event.EventData;
import com.example.bomberman.model.event.Topic;
import com.example.bomberman.util.JsonHelper;


public class Broker {
    private static final Logger log = LoggerFactory.getLogger(Broker.class);

    private static final Broker instance = new Broker();
    private ConnectionPool connectionPool;

    public static Broker getInstance() {
        return instance;
    }

    private Broker() {

    }

    public void receive(@NotNull WebSocketSession session, @NotNull String msg) {
        log.info("RECEIVED: " + msg);
        EventData eventData = JsonHelper.fromJson(msg, EventData.class);
        //TODO TASK2 implement message processing
    }

    public void send(@NotNull String player, @NotNull Topic topic, @NotNull Object object) {
        String message = JsonHelper.toJson(new EventData(topic, JsonHelper.getJsonNode(object)));
        WebSocketSession session = connectionPool.getSession(player);
        connectionPool.send(session, message);
    }

    public void broadcast(@NotNull Topic topic, @NotNull Object object) {
        String message = JsonHelper.toJson(new EventData(topic, JsonHelper.getJsonNode(object)));
        connectionPool.broadcast(message);
    }

}
