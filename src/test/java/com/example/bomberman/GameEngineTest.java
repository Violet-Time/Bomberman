package com.example.bomberman;

import com.example.bomberman.model.game.Vector2;
import com.example.bomberman.model.game.Rect;
import com.example.bomberman.model.game.Size;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GameEngineTest {
    @Test
    void intersectRectTest() {
        Size size = new Size(10, 10);
        Rect rectA = new Rect(size);
        rectA.setBitmapPosition(new Vector2(10, 10));
        /*Rect rectB = new Rect(size 21, 20, 31, 10);
        rectB.setBitmapPosition();
        assertFalse(GameEngine.intersectRect(rectA, rectB));
        assertFalse(GameEngine.intersectRect(rectB, rectA));

        rectB = new Rect(0, 20, 10,10);
        assertTrue(GameEngine.intersectRect(rectA, rectB));
        assertTrue(GameEngine.intersectRect(rectB, rectA));

        rectB = new Rect(1, 20, 11,10);
        assertTrue(GameEngine.intersectRect(rectA, rectB));
        assertTrue(GameEngine.intersectRect(rectB, rectA));*/


/*
        assertTrue(GameEngine.intersectRect(rectA, rectB));

        rectB = new Rect(19, 20, 29,10);

        assertTrue(GameEngine.intersectRect(rectA, rectB));


        rectB = new Rect(15, 25, 25,15);

        assertTrue(GameEngine.intersectRect(rectA, rectB));

        rectB = new Rect(19, 20, 29,10);

        assertTrue(GameEngine.intersectRect(rectA, rectB));*/
    }
}
