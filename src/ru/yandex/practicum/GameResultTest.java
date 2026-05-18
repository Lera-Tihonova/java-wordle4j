package ru.yandex.practicum;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameResultTest {

    @Test
    void testGameResultValues() {
        assertEquals(3, GameResult.values().length);
        assertTrue(GameResult.WIN == GameResult.valueOf("WIN"));
        assertTrue(GameResult.LOSE == GameResult.valueOf("LOSE"));
        assertTrue(GameResult.CONTINUE == GameResult.valueOf("CONTINUE"));
    }
}
