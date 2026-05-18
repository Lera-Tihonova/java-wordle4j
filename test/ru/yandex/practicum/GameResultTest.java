package ru.yandex.practicum;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameResultTest {

    @Test
    void testGameResultValues() {
        assertEquals(3, GameResult.values().length);
        assertEquals(GameResult.WIN, GameResult.valueOf("WIN"));
        assertEquals(GameResult.LOSE, GameResult.valueOf("LOSE"));
        assertEquals(GameResult.CONTINUE, GameResult.valueOf("CONTINUE"));
    }
}
