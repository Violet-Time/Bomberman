package com.example.bomberman.controller.network;

import com.example.bomberman.model.GameIdName;
import com.example.bomberman.model.GameSession;
import com.example.bomberman.model.message.Message;
import com.example.bomberman.repos.ConnectionRepository;
import com.example.bomberman.repos.GameRepository;
import com.example.bomberman.util.JsonHelper;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;


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

        log.info("New connection {} {}",session.getId(), session.getUri());
        try {
            GameIdName gameIdName = new GameIdName(splitQuery(Objects.requireNonNull(session.getUri())));
            GameSession gameSession = gameRepository.getSession(gameIdName.gameId());
            if (gameSession == null) {
                session.close();
                return;
            }
            gameSession.connectSessionPlayer(session, gameIdName.playerName());
            connectionRepository.put(session, gameIdName);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, TextMessage message) {
        try {
            Message message1 = JsonHelper.fromJson(message.getPayload(), Message.class);
            GameIdName gameIdName = connectionRepository.getConnection(session);
            if (gameIdName == null) {
                return;
            }
            message1.setNamePlayer(gameIdName.playerName());
            gameRepository.getSession(gameIdName.gameId()).getInputQueue().add(message1);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        try {
            log.info("Connection close {}", session.getId());
            GameIdName gameIdName = connectionRepository.getConnection(session);
            gameRepository.getSession(gameIdName.gameId()).connectionClose(gameIdName.playerName());
            super.afterConnectionClosed(session, status);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public Map<String, List<String>> splitQuery(URI uri) {
        if (uri.getQuery() == null || uri.getQuery().isBlank()) {
            return Collections.emptyMap();
        }
        return Arrays.stream(uri.getQuery().split("&"))
                .map(this::splitQueryParameter)
                .collect(Collectors.groupingBy(AbstractMap.SimpleImmutableEntry::getKey, LinkedHashMap::new, mapping(Map.Entry::getValue, toList())));
    }

    public AbstractMap.SimpleImmutableEntry<String, String> splitQueryParameter(String it) {
        final int idx = it.indexOf("=");
        final String key = idx > 0 ? it.substring(0, idx) : it;
        final String value = idx > 0 && it.length() > idx + 1 ? it.substring(idx + 1) : null;
        return new AbstractMap.SimpleImmutableEntry<>(
                URLDecoder.decode(key, StandardCharsets.UTF_8),
                URLDecoder.decode(value, StandardCharsets.UTF_8)
        );
    }
}
