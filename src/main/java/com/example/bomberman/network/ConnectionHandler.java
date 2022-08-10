package com.example.bomberman.network;

import com.example.bomberman.message.Message;
import com.example.bomberman.repos.GameRepository;
import com.example.bomberman.repos.GameRepositoryImpl;
import com.example.bomberman.util.JsonHelper;
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
    private GameRepository gameRepository;

    public ConnectionHandler(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        log.info("new session {}", session.getUri());

        Map<String, List<String>> query = splitQuery(Objects.requireNonNull(session.getUri()));

        if (query.get("gameId") != null && query.get("name") != null) {

            log.debug("gameId = {} name = {}", query.get("gameId").get(0), query.get("name").get(0));

            gameRepository.getSession(Long.valueOf(query.get("gameId").get(0))).connectSessionPlayer(session, query.get("name").get(0));
        }

        //connectionPool.add(query.get("name").get(0), session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        //log.info(message.toString());
        Message message1 = JsonHelper.fromJson(message.getPayload(), Message.class);

        /*Message message1 = new ObjectMapper()
                .readerFor(Message.class)
                        .readValue(message.getPayload());*/
        Map<String, List<String>> query = splitQuery(Objects.requireNonNull(session.getUri()));
        message1.setPlayerName(query.get("name").get(0));
        gameRepository.getSession(Long.valueOf(query.get("gameId").get(0))).getInputQueue().add(message1);
        log.info("Message: {}", message1.toString());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Connection close");
        super.afterConnectionClosed(session, status);
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
