package com.example.bomberman.controller;

import com.example.bomberman.network.ConnectionHandler;
import com.example.bomberman.repos.GameRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class EventsController implements WebSocketConfigurer {

    private GameRepositoryImpl gameRepository;

    public EventsController(GameRepositoryImpl gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ConnectionHandler(gameRepository), "/events/connect")
                .setAllowedOrigins("*");
    }
}
