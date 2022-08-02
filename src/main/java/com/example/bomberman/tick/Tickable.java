package com.example.bomberman.tick;

/**
 * Any game object that changes with time
 */
public interface Tickable extends Comparable {
    /**
     * Applies changes to game objects that happen after elapsed time
     */
    void tick(long elapsed);
}
