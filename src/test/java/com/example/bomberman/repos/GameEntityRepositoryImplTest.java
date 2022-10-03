package com.example.bomberman.repos;
import com.example.bomberman.service.game.core.logic.Vector2;
import com.example.bomberman.service.game.core.logic.entity.pawn.Bot;
import com.example.bomberman.repos.impl.GameEntityRepositoryImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GameEntityRepositoryImplTest {
    public static final int TILE_SIZE = 32;
    private static final int TILES_X = 27;
    private static final int TILES_Y = 17;

    @Test
    void addAndRemoveTest() {
        GameEntityRepository gameEntityRepository = new GameEntityRepositoryImpl();
        Bot bot = new Bot(gameEntityRepository);
        bot.setEntityPosition(new Vector2(1, TILES_Y - 2));

        Bot bot1 = gameEntityRepository.getBots(bot.getEntityPosition()).stream().findFirst().orElse(null);

        Assertions.assertEquals(bot, bot1);

        gameEntityRepository.removeGameEntity(bot);

        Assertions.assertTrue(gameEntityRepository.getAllBots().isEmpty());

        /*System.out.println(bot);
        System.out.println(bot1);*/
    }
}
