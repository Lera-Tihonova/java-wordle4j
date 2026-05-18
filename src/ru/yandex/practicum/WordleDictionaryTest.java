package ru.yandex.practicum;

import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WordleDictionaryTest {

    private static WordleDictionary dictionary;

    @BeforeAll
    static void setUp() {
        Set<String> words = new HashSet<>(Arrays.asList(
            "герой", "горец", "гонец", "город", "голос"
        ));
        dictionary = new WordleDictionary(words);
    }

    @Test
    @Order(1)
    void testContainsWord() {
        assertTrue(dictionary.containsWord("герой"));
        assertTrue(dictionary.containsWord("горец"));
        assertFalse(dictionary.containsWord("комар"));
    }

    @Test
    @Order(2)
    void testNormalizeWord() {
        assertEquals("еж", WordleDictionary.normalizeWord("ёж"));
        assertEquals("ежик", WordleDictionary.normalizeWord("ёжик"));
        assertEquals("герой", WordleDictionary.normalizeWord("ГЕРОЙ"));
        assertEquals("горец", WordleDictionary.normalizeWord("  ГоРеЦ  "));
    }

    @Test
    @Order(3)
    void testGetHintPattern() {
        String pattern = WordleDictionary.getHintPattern("гонец", "герой");
        assertEquals("+^-^-", pattern);

        pattern = WordleDictionary.getHintPattern("город", "герой");
        assertNotNull(pattern);
    }

    @Test
    @Order(4)
    void testGetRandomWord() {
        String word = dictionary.getRandomWord();
        assertTrue(dictionary.containsWord(word));
    }

    @Test
    @Order(5)
    void testSize() {
        assertEquals(5, dictionary.size());
    }

    @Test
    @Order(6)
    void testGetPossibleWords() {
        Set<Character> mustContain = new HashSet<>(Arrays.asList('г', 'о'));
        Map<Integer, Character> exactMatches = new HashMap<>();
        exactMatches.put(0, 'г');
        Set<Character> notContain = new HashSet<>();

        List<String> possible = dictionary.getPossibleWords(mustContain, exactMatches, notContain);
        assertEquals(5, possible.size());
    }
}
