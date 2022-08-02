package com.example.bomberman;

import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration
public class Config {

    /*@Bean
    public ConnectionQueue connectionQueue() {
        return new ConnectionQueue();
    }

    @Bean
    public GameRepository gameRepository() {
        return new GameRepository();
    }

    @Bean
    public ConnectionPool connectionPool() {
        return new ConnectionPool();
    }

    @Bean
    public GameService gameService(GameRepository gameRepository, ConnectionPool connectionPool) {
        return new GameService(gameRepository, connectionPool);
    }

    @Bean
    public MatchMaker matchMaker(GameService gameService, ConnectionQueue connectionQueue) {
        return new MatchMaker(gameService, connectionQueue);
    }

    @Bean
    public ConnectionHandler connectionHandler(ConnectionPool connectionPool) {
        return new ConnectionHandler(connectionPool);
    }*/

    /*@Bean
    public MatchMakerController matchMakerController(MatchMaker matchMaker) {
        return new MatchMakerController(matchMaker);
    }*/
}
