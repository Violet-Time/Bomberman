package com.example.bomberman;

import com.example.bomberman.model.message.Message;
import com.example.bomberman.controller.network.ConnectionPool;
import com.example.bomberman.service.tick.gameMechanics.GameMechanics;
import com.example.bomberman.service.tick.InputQueue;
import com.example.bomberman.service.tick.Replicator;
import com.example.bomberman.util.JsonHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class GameMechanicsTest {

    static GameMechanics gameMechanics;

    @BeforeAll
    public static void setup() {
        InputQueue inputQueue = new InputQueue();
        ConnectionPool connectionPool = new ConnectionPool();
        Replicator replicator = new Replicator(connectionPool);
        gameMechanics = new GameMechanics(inputQueue, replicator);
    }

    void levelView() {

    }

    @Test
    void move() {

        Message message = JsonHelper.fromJson("{\"topic\":\"MOVE\",\"data\":{\"direction\":\"UP\"}}", Message.class);

        System.out.println(message.getData().get("direction").asText());

        //gameMechanics.generateGameObjects();

    }
}
