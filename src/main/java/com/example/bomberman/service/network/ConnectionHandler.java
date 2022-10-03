package com.example.bomberman.service.network;

import com.example.bomberman.service.GameSession;
import com.example.bomberman.repos.ConnectionRepository;
import com.example.bomberman.repos.GameRepository;
import com.example.bomberman.util.QueryHelper;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;


@Component
public class ConnectionHandler extends TextWebSocketHandler implements WebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(ConnectionHandler.class);
    private final GameRepository gameRepository;
    private final ConnectionRepository connectionRepository;

    public ConnectionHandler(GameRepository gameRepository, ConnectionRepository connectionRepository) {
        this.gameRepository = gameRepository;
        this.connectionRepository = connectionRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

        log.info("New connection {} {}", session.getId(), session.getUri());
        try {
            Map<String, List<String>> query = QueryHelper.splitQuery(Objects.requireNonNull(session.getUri()));

            long gameId = Long.parseLong(query.get("gameId").get(0));
            String namePlayer = query.get("name").get(0);

            GameSession gameSession = gameRepository.getSession(gameId);

            if (gameSession == null) {
                log.warn("Game session {} not found", namePlayer);
                session.close();
                return;
            }
            gameSession.addSocket(session, namePlayer);
            connectionRepository.addConnection(session, gameId);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) {
        try {
            Long gameId = connectionRepository.getConnection(session);
            if (gameId == null) return;
            GameSession gameSession = gameRepository.getSession(gameId);
            gameSession.getBroker().receive(session, message.getPayload());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) {
        try {
            log.info("Connection close {}", session.getId());
            Long gameId = connectionRepository.getConnection(session);
            gameRepository.getSession(gameId).connectionClose(session);
            connectionRepository.removeConnection(session);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


}
