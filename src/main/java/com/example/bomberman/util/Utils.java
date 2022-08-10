package com.example.bomberman.util;

import com.example.bomberman.model.game.Vector2;
import com.example.bomberman.tick.Game.GameEngine;

public class Utils {
    /**
     * Returns true if positions are equal.
     */
    public static boolean comparePositions(Vector2 pos1, Vector2 pos2) {
        return pos1.getX() == pos2.getX() && pos1.getY() == pos2.getY();
    }


    /**
     * Convert bitmap pixels position to entity on grid position.
     */
    public static Vector2 convertToEntityPosition(Vector2 pixels) {
        Vector2 position = new Vector2(Math.round(pixels.getX() / GameEngine.TILE_SIZE),
                                    Math.round(pixels.getY() /GameEngine.TILE_SIZE));
        return position;
    }

    /**
    * Convert entity on grid position to bitmap pixels position.
    */
    public static Vector2 convertToBitmapPosition(Vector2 entity) {
        Vector2 position = new Vector2(Math.round(entity.getX() * GameEngine.TILE_SIZE),
                Math.round(entity.getY() * GameEngine.TILE_SIZE));
        return position;
    }
}
