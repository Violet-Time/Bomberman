package com.example.bomberman.controller;

import com.example.bomberman.service.network.ConnectionHandler;
import com.example.bomberman.repos.ConnectionRepository;
import com.example.bomberman.repos.impl.GameRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class EventsController implements WebSocketConfigurer {

    private final GameRepositoryImpl gameRepository;
    private final ConnectionRepository connectionRepository;

    public EventsController(GameRepositoryImpl gameRepository, ConnectionRepository connectionRepository) {
        this.gameRepository = gameRepository;
        this.connectionRepository = connectionRepository;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ConnectionHandler(gameRepository, connectionRepository), "/events/connect")
                .setAllowedOrigins("*");
    }
}
