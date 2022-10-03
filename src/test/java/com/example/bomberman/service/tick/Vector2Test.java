package com.example.bomberman.service.tick;

import com.example.bomberman.service.game.core.logic.Vector2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertTrue;

public class Vector2Test {
    @Test
    void equalsTest() {
        Vector2 vectorFirst = new Vector2(2, 3);
        Vector2 vectorSecond = new Vector2(1, 1);

        Assertions.assertNotEquals(vectorFirst, vectorSecond);

        vectorSecond = vectorSecond.add(new Vector2(1,2));

        Assertions.assertEquals(vectorFirst, vectorSecond);
    }
}
