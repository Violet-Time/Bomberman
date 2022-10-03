package com.example.bomberman.service.tick;


import com.example.bomberman.service.game.core.logic.Rect;
import com.example.bomberman.service.game.core.logic.Size;
import com.example.bomberman.service.game.core.logic.Vector2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RectTest {

    @Test
    void equalsTest() {
        Size sizeA = new Size(10, 10);
        Rect rectA = new Rect(sizeA);
        rectA.setBitmapPosition(new Vector2(10, 10));

        Size sizeB = new Size(12, 9);
        Rect rectB = new Rect(sizeB);
        rectB.setBitmapPosition(new Vector2(10, 20));

        Assertions.assertNotEquals(rectA, rectB);

        rectB = new Rect(new Size(10, 10));
        rectB.setBitmapPosition(new Vector2(10, 10));

        Assertions.assertEquals(rectA, rectB);

    }

    @Test
    void collidingTest() {
        Size sizeA = new Size(10, 10);
        Rect rectA = new Rect(sizeA);
        rectA.setBitmapPosition(new Vector2(10, 10));

        Size sizeB = new Size(12, 9);
        Rect rectB = new Rect(sizeB);
        //false
        //top
        rectB.setBitmapPosition(new Vector2(10, 20));
        Assertions.assertFalse(rectA.isColliding(rectB));
        Assertions.assertFalse(rectB.isColliding(rectA));
        //top right
        rectB.setBitmapPosition(new Vector2(20, 20));
        Assertions.assertFalse(rectA.isColliding(rectB));
        Assertions.assertFalse(rectB.isColliding(rectA));
        //right
        rectB.setBitmapPosition(new Vector2(20, 10));
        Assertions.assertFalse(rectA.isColliding(rectB));
        Assertions.assertFalse(rectB.isColliding(rectA));
        //down right
        rectB.setBitmapPosition(new Vector2(20, 1));
        Assertions.assertFalse(rectA.isColliding(rectB));
        Assertions.assertFalse(rectB.isColliding(rectA));
        //down
        rectB.setBitmapPosition(new Vector2(9, 1));
        Assertions.assertFalse(rectA.isColliding(rectB));
        Assertions.assertFalse(rectB.isColliding(rectA));
        //down left
        rectB.setBitmapPosition(new Vector2(-2, 1));
        Assertions.assertFalse(rectA.isColliding(rectB));
        Assertions.assertFalse(rectB.isColliding(rectA));
        //left
        rectB.setBitmapPosition(new Vector2(-2, 10));
        Assertions.assertFalse(rectA.isColliding(rectB));
        Assertions.assertFalse(rectB.isColliding(rectA));
        //top left
        rectB.setBitmapPosition(new Vector2(0, 20));
        Assertions.assertFalse(rectA.isColliding(rectB));
        Assertions.assertFalse(rectB.isColliding(rectA));

        //true
        //top
        rectB.setBitmapPosition(new Vector2(10, 19));
        Assertions.assertTrue(rectA.isColliding(rectB));
        Assertions.assertTrue(rectB.isColliding(rectA));
        //top right
        rectB.setBitmapPosition(new Vector2(19, 19));
        Assertions.assertTrue(rectA.isColliding(rectB));
        Assertions.assertTrue(rectB.isColliding(rectA));
        //right
        rectB.setBitmapPosition(new Vector2(19, 10));
        Assertions.assertTrue(rectA.isColliding(rectB));
        Assertions.assertTrue(rectB.isColliding(rectA));
        //down right
        rectB.setBitmapPosition(new Vector2(19, 2));
        Assertions.assertTrue(rectA.isColliding(rectB));
        Assertions.assertTrue(rectB.isColliding(rectA));
        //down
        rectB.setBitmapPosition(new Vector2(9, 2));
        Assertions.assertTrue(rectA.isColliding(rectB));
        Assertions.assertTrue(rectB.isColliding(rectA));
        //down left
        rectB.setBitmapPosition(new Vector2(-1, 2));
        Assertions.assertTrue(rectA.isColliding(rectB));
        Assertions.assertTrue(rectB.isColliding(rectA));
        //left
        rectB.setBitmapPosition(new Vector2(-1, 10));
        Assertions.assertTrue(rectA.isColliding(rectB));
        Assertions.assertTrue(rectB.isColliding(rectA));
        //top left
        rectB.setBitmapPosition(new Vector2(-1, 19));
        Assertions.assertTrue(rectA.isColliding(rectB));
        Assertions.assertTrue(rectB.isColliding(rectA));
    }
}
