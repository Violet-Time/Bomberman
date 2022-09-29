package com.example.bomberman;

import com.example.bomberman.model.event.EventData;
import com.example.bomberman.service.tick.gameMechanics.GameMechanics;
import com.example.bomberman.util.JsonHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
@Disabled
public class GameMechanicsTest {

    static GameMechanics gameMechanics;

    @BeforeAll
    public static void setup() {
        /*InputQueue inputQueue = new InputQueue();
        ConnectionPool connectionPool = new ConnectionPool();
        Replicator replicator = new Replicator(connectionPool);
        gameMechanics = new GameMechanics(inputQueue, replicator);*/
    }

    void levelView() {

    }

    @Test
    void move() {

        EventData eventData = JsonHelper.fromJson("{\"topic\":\"MOVE\",\"data\":{\"direction\":\"UP\"}}", EventData.class);

        System.out.println(eventData.getData().get("direction").asText());

        //gameMechanics.generateGameObjects();

    }
}
