package com.example.bomberman.service;

import com.example.bomberman.service.network.Broker;
import org.springframework.web.socket.WebSocketSession;


public interface GameSession {

    boolean isFullPlayers();

    boolean addPlayer(String namePlayer);

    boolean addSocket(WebSocketSession session, String name);

    long getId();

    void start();

    void connectionClose(WebSocketSession session);

    Broker getBroker();

    int getPlayersInGame();
}
