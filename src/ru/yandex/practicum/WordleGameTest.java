package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.WordNotInDictionaryException;
import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

class WordleGameTest {
    
    private WordleDictionary dictionary;
    private WordleGame game;
    private PrintWriter testLogger;
    private List<String> testWords;
    
    @BeforeEach
    void setUp() {
        try {
            Set<String> words = loadRealDictionary();
            dictionary = new WordleDictionary(words);
            testLogger = new PrintWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
            game = new WordleGame(dictionary, testLogger);
            testWords = new ArrayList<>(words);
        } catch (Exception e) {
            fail("Не удалось загрузить словарь: " + e.getMessage());
        }
    }
    
    private Set<String> loadRealDictionary() throws IOException {
        Set<String> words = new HashSet<>();
        String dictPath = "words_ru.txt";
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(dictPath), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String normalized = WordleDictionary.normalizeWord(line);
                if (normalized.length() == 5 && normalized.matches("[а-я]+")) {
                    words.add(normalized);
                }
            }
        }
        return words;
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
        boolean wordInDictionary = dictionary.containsWord(game.getSecretWord());
        assertTrue(wordInDictionary, "Загаданное слово должно быть в словаре: " + game.getSecretWord());
    }
    
    @Test
    void testMakeCorrectGuess() throws WordNotInDictionaryException {
        String secretWord = game.getSecretWord();
        GameResult result = game.makeGuess(secretWord);
        assertEquals(GameResult.WIN, result, "Правильное слово должно приводить к победе");
        assertTrue(game.isGameOver(), "Игра должна закончиться после победы");
    }
    
    @Test
    void testMakeIncorrectGuess() throws WordNotInDictionaryException {
        String secretWord = game.getSecretWord();
        String wrongGuess = null;
        
        for (String word : testWords) {
            if (!word.equals(secretWord)) {
                wrongGuess = word;
                break;
            }
        }
        
        if (wrongGuess == null) {
            return;
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
        
        exception = assertThrows(WordNotInDictionaryException.class, () -> {
            game.makeGuess("длинноеслово");
        });
        assertEquals("Слово должно быть из 5 букв", exception.getMessage());
    }
    
    @Test
    void testWordNotInDictionary() {
        Exception exception = assertThrows(WordNotInDictionaryException.class, () -> {
            game.makeGuess("zzzzz");
        });
        assertEquals("Слова нет в словаре", exception.getMessage());
    }
    
    @Test
    void testGetHint() {
        String hint = game.getHint();
        if (hint != null) {
            assertEquals(5, hint.length(), "Подсказка должна быть длиной 5 букв");
            assertTrue(dictionary.containsWord(hint), "Подсказка должна быть словом из словаря");
        }
    }
    
    @Test
    void testGetLastHint() throws WordNotInDictionaryException {
        assertEquals("", game.getLastHint(), "Изначально последней подсказки нет");
        
        if (!testWords.isEmpty()) {
            game.makeGuess(testWords.get(0));
            assertNotEquals("", game.getLastHint(), "После хода должна появиться подсказка");
        }
    }
    
    @Test
    void testSixAttemptsLose() throws WordNotInDictionaryException {
        if (testWords.size() < 2) {
            return;
        }
        
        Set<String> twoWords = new HashSet<>(testWords.subList(0, 2));
        WordleDictionary twoDict = new WordleDictionary(twoWords);
        WordleGame twoGame = new WordleGame(twoDict, testLogger);
        
        String secret = twoGame.getSecretWord();
        String wrongWord = secret.equals(twoWords.iterator().next())
            ? twoWords.toArray(new String[0])[1] 
            : twoWords.iterator().next();
        
        for (int i = 0; i < 5; i++) {
            GameResult result = twoGame.makeGuess(wrongWord);
            assertNotEquals(GameResult.WIN, result);
            assertFalse(twoGame.isGameOver());
        }
        
        assertEquals(1, twoGame.getRemainingAttempts());
        
        GameResult finalResult = twoGame.makeGuess(wrongWord);
        assertEquals(GameResult.LOSE, finalResult);
        assertTrue(twoGame.isGameOver());
    }
}
