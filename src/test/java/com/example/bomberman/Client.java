package com.example.bomberman;

import com.example.bomberman.service.MatchMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Client {
    private String uri = "ws://localhost:{port}/events/connect?gameId={gameId}&name={name}";
    private static final Logger log = LoggerFactory.getLogger(Client.class);
    WebSocketClient client = new StandardWebSocketClient();
    WebSocketSession session = null;

    public Client(String port, String gameId, String name) {

        log.info("Create WebSocketClient gameId={}, name={}", gameId, name);
        // The socket that receives events
        EventHandler socket = new EventHandler();
        // Make a handshake with server
        ListenableFuture<WebSocketSession> fut = client.doHandshake(socket, uri, port, gameId, name);
        // Wait for Connect
        try {
            session = fut.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String message) {
        try {
            session.sendMessage(new TextMessage(message));
        } catch (IOException e) {
            close();
        }
    }

    public void close() {
        try {

            session.close();

        } catch (Throwable t) {
            t.printStackTrace(System.err);
        } finally {
            try {
                if (session != null) {
                    session.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    public static void main(String[] args) {
        Client client1 = new Client("8080", "0", "a");
        client1.close();
    }
}

