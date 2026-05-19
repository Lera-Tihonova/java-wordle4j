package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.WordNotInDictionaryException;
import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class WordleGameTest {
    
    private WordleDictionary dictionary;
    private WordleGame game;
    private PrintWriter testLogger;
    
    @BeforeEach
    void setUp() {
        try {
            Set<String> words = new HashSet<>(Arrays.asList(
                "герой", "горец", "гонец", "город", "голос", "котёл"
            ));
            dictionary = new WordleDictionary(words);
            testLogger = new PrintWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
            game = new WordleGame(dictionary, testLogger);
        } catch (Exception e) {
            fail("Не удалось создать игру: " + e.getMessage());
        }
    }
    
    @AfterEach
    void tearDown() {
        testLogger.flush();
    }
    
    @Test
    void testGameInitialization() {
        assertNotNull(game);
        assertEquals(6, game.getRemainingAttempts());
        assertFalse(game.isGameOver());
        assertNotNull(game.getSecretWord());
    }
    
    @Test
    void testMakeCorrectGuess() throws WordNotInDictionaryException {
        String secretWord = game.getSecretWord();
        GameResult result = game.makeGuess(secretWord);
        assertEquals(GameResult.WIN, result);
        assertTrue(game.isGameOver());
    }
    
    @Test
    void testMakeIncorrectGuess() throws WordNotInDictionaryException {
        String secretWord = game.getSecretWord();
        String wrongGuess = secretWord.equals("герой") ? "горец" : "герой";
        
        GameResult result = game.makeGuess(wrongGuess);
        assertNotEquals(GameResult.WIN, result);
        assertEquals(5, game.getRemainingAttempts());
    }
    
    @Test
    void testInvalidGuessLength() {
        Exception exception = assertThrows(WordNotInDictionaryException.class, () -> {
            game.makeGuess("кот");
        });
        assertEquals("Слово должно быть из 5 букв", exception.getMessage());
    }
    
    @Test
    void testWordNotInDictionary() {
        Exception exception = assertThrows(WordNotInDictionaryException.class, () -> {
            game.makeGuess("абвгд");
        });
        assertEquals("Слова нет в словаре", exception.getMessage());
    }
    
    @Test
    void testGetLastHint() throws WordNotInDictionaryException {
        assertEquals("", game.getLastHint());
        
        game.makeGuess("герой");
        assertNotEquals("", game.getLastHint());
    }
    
    @Test
    void testSixAttemptsLose() throws WordNotInDictionaryException {
        Set<String> twoWords = new HashSet<>(Arrays.asList("герой", "горец"));
        WordleDictionary twoDict = new WordleDictionary(twoWords);
        WordleGame twoGame = new WordleGame(twoDict, testLogger);
        
        String secret = twoGame.getSecretWord();
        String wrongWord = secret.equals("герой") ? "горец" : "герой";
        
        for (int i = 0; i < 6; i++) {
            GameResult result = twoGame.makeGuess(wrongWord);
            if (result == GameResult.LOSE) {
                assertTrue(twoGame.isGameOver());
                return;
            }
        }
        fail("Игра не закончилась после 6 попыток");
    }
}
