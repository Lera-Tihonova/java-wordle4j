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
    private List<String> testWords;
    
    @BeforeEach
    void setUp() {
        // Фиксированный тестовый словарь из 6 слов
        Set<String> words = new HashSet<>(Arrays.asList(
            "герой", "горец", "гонец", "город", "голос", "котёл"
        ));
        dictionary = new WordleDictionary(words);
        testWords = new ArrayList<>(words);
        testLogger = new PrintWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
        game = new WordleGame(dictionary, testLogger);
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
        
        String secret = game.getSecretWord();
        boolean contains = dictionary.containsWord(secret);
        assertTrue(contains, "Слово '" + secret + "' должно быть в словаре " + testWords);
    }
    
    @Test
    void testMakeCorrectGuess() throws WordNotInDictionaryException {
        String secretWord = game.getSecretWord();
        System.out.println("DEBUG: secretWord = " + secretWord);
        System.out.println("DEBUG: dictionary contains = " + dictionary.containsWord(secretWord));
        GameResult result = game.makeGuess(secretWord);
        assertEquals(GameResult.WIN, result);
        assertTrue(game.isGameOver());
    }
    
    @Test
    void testMakeIncorrectGuess() throws WordNotInDictionaryException {
        String secretWord = game.getSecretWord();
        
        String wrongGuess = testWords.get(0);
        if (wrongGuess.equals(secretWord)) {
            wrongGuess = testWords.get(1);
        }
        
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
        
        // Делаем ход первым словом из словаря
        game.makeGuess(testWords.get(0));
        assertNotEquals("", game.getLastHint());
    }
    
    @Test
    void testSixAttemptsLose() throws WordNotInDictionaryException {
        
        Set<String> twoWords = new HashSet<>(Arrays.asList("герой", "горец"));
        WordleDictionary twoDict = new WordleDictionary(twoWords);
        WordleGame twoGame = new WordleGame(twoDict, testLogger);
        
        String secret = twoGame.getSecretWord();
        String wrongWord = secret.equals("герой") ? "горец" : "герой";
              
        for (int i = 0; i < 5; i++) {
            twoGame.makeGuess(wrongWord);
            assertFalse(twoGame.isGameOver());
        }
        
        assertEquals(1, twoGame.getRemainingAttempts());
        
        GameResult finalResult = twoGame.makeGuess(wrongWord);
        assertEquals(GameResult.LOSE, finalResult);
        assertTrue(twoGame.isGameOver());
    }
}
