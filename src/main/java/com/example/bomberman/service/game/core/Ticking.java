package com.example.bomberman.service.game.core;

/**
 * Any game object that changes with time
 */
public interface Ticking {
    /**
     * Applies changes to game objects that happen after elapsed time
     */
    void tick(long elapsed);
}
