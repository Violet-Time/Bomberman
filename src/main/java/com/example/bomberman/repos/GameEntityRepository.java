package com.example.bomberman.repos;

import com.example.bomberman.service.game.core.logic.entity.GameEntity;
import com.example.bomberman.service.game.core.logic.Vector2;
import com.example.bomberman.service.game.core.logic.entity.Bomb;
import com.example.bomberman.service.game.core.logic.entity.Fire;
import com.example.bomberman.service.game.core.logic.entity.pawn.Bot;
import com.example.bomberman.service.game.core.logic.entity.pawn.Pawn;
import com.example.bomberman.service.game.core.logic.entity.pawn.Player;
import com.example.bomberman.service.game.core.logic.entity.bonus.Bonus;
import com.example.bomberman.service.game.core.logic.entity.tile.Material;
import com.example.bomberman.service.game.core.logic.entity.tile.Tile;

import java.util.List;

public interface GameEntityRepository {

    boolean addPlayer(Player player);
    boolean addBot(Bot bot);
    boolean addTile(Tile tile);
    boolean addBomb(Bomb bomb);
    boolean addBonus(Bonus bonus);
    boolean addFire(Fire fire);
    boolean addGameEntity(GameEntity gameEntity);

    Player getPlayer(String namePlayer);
    List<Player> getPlayers(Vector2 entityPosition);

    List<Bot> getBots(Vector2 entityPosition);
    List<Bomb> getBombs(Vector2 entityPosition);
    List<Bonus> getBonuses(Vector2 entityPosition);
    List<Fire> getFires(Vector2 entityPosition);

    /**
     * Returns tile at given position.
     */
    Tile getTile(Vector2 position);

    List<Player> getAllPlayers();
    List<Bot> getAllBots();
    List<Tile> getAllTitles();
    List<Bomb> getAllBombs();
    List<Bonus> getAllBonuses();
    List<Fire> getAllFires();
    List<GameEntity> getAllGameEntities();
    //List<GameEntity> getGameEntitiesForSent();
    List<GameEntity> getGameEntitiesOnMapGrid(Vector2 vector2);

    List<Pawn> getPlayersAndBots();
    List<Pawn> getPlayersAndBots(Vector2 vector2);

    /**
     * Returns tile material at given position.
     */
    Material getTileMaterial(Vector2 position);

    boolean removeGameEntity(GameEntity gameEntity);
    boolean removePlayer(Player player);
    boolean removeBot(Bot bot);
    boolean removeTile(Tile tile);
    boolean removeBomb(Bomb bomb);
    boolean removeBonus(Bonus bonus);
    boolean removeFire(Fire fire);

    int playersCount();

    long generateId();
}
