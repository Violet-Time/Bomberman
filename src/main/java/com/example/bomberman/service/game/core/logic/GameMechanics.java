package com.example.bomberman.service.game.core.logic;

import com.example.bomberman.repos.GameEntityRepository;
import com.example.bomberman.service.game.core.Ticking;
import com.example.bomberman.service.game.core.logic.entity.pawn.Player;
import com.example.bomberman.service.network.ConnectionPool;
import com.example.bomberman.service.game.core.InputEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class GameMechanics implements Ticking {

    private final GameEntityRepository gameEntityRepository;

    private final InputEngine inputEngine;

    private GameEngine gameEngine;

    private final Logger log = LoggerFactory.getLogger(GameMechanics.class);

    public GameMechanics(InputEngine inputEngine, GameEntityRepository gameEntityRepository) {
        this.inputEngine = inputEngine;
        this.gameEntityRepository = gameEntityRepository;
    }

    public void createGame() {
        this.gameEngine = new GameEngine(gameEntityRepository);
        this.gameEngine.setup();
    }

    @Override
    public void tick(long elapsed) {
        gameEngine.update(elapsed);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return (o == null || getClass() != o.getClass());

    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass());
    }
}
