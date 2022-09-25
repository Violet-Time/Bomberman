package com.example.bomberman.controller;

import com.example.bomberman.repos.ConnectionQueue;
import com.example.bomberman.repos.GameRepositoryImpl;
import com.example.bomberman.service.GameServiceImpl;
import com.example.bomberman.service.MatchMaker;
import org.junit.Ignore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.*;

import static org.junit.Assert.assertEquals;

@Disabled
public class MatchMakerControllerTest {

    static MatchMakerController matchMakerController;

    @BeforeAll
    public static void setup() {
        ConnectionQueue connectionQueue = new ConnectionQueue();
        GameRepositoryImpl gameRepository = new GameRepositoryImpl();
        GameServiceImpl gameService = new GameServiceImpl(gameRepository);
        MatchMaker matchMaker = new MatchMaker(gameService, connectionQueue);
        //matchMakerController = new MatchMakerController(matchMaker);
    }

    @Ignore
    void join() {
        Assertions.assertEquals("0", matchMakerController.join("dfsa"));
    }
}
