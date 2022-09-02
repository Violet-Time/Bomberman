package com.example.bomberman.service.tick.gameMechanics;

import com.example.bomberman.repos.GameEntityRepository;
import com.example.bomberman.repos.impl.GameEntityRepositoryImpl;
import com.example.bomberman.service.tick.gameMechanics.dynamic.pawn.Bot;
import com.example.bomberman.service.tick.gameMechanics.dynamic.pawn.Pawn;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GameEntityTest {

    public static final int TILE_SIZE = 32;
    private static final int TILES_X = 27;
    private static final int TILES_Y = 17;

    @Test
    void setBitmapPositionTest() {
        GameEntityRepository gameEntityRepository = new GameEntityRepositoryImpl();
        Vector2 first = new Vector2(1, this.TILES_Y - 2);
        Bot bot = new Bot(gameEntityRepository, first);
        Assertions.assertFalse(gameEntityRepository.getBots(first).isEmpty());

        bot.setBitmapPosition(bot.getBitmapPosition().add(1,1));
        Assertions.assertFalse(gameEntityRepository.getBots(first).isEmpty());

        bot.setBitmapPosition(bot.getBitmapPosition().add(30,30));
        Assertions.assertTrue(gameEntityRepository.getBots(first).isEmpty());
        Assertions.assertFalse(gameEntityRepository.getBots(bot.getEntityPosition()).isEmpty());
    }
}
