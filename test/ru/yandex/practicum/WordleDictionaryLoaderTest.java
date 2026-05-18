package ru.yandex.practicum;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.*;

class WordleDictionaryLoaderTest {
    
    private static final String TEST_DICT_PATH = "test_words.txt";
    private PrintWriter testLogger;
    
    @BeforeEach
    void setUp() throws IOException {
        testLogger = new PrintWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
        
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(TEST_DICT_PATH), StandardCharsets.UTF_8))) {
            writer.println("герой");
            writer.println("горец");
            writer.println("длинное");  // 7 букв
            writer.println("кот");      // 3 буквы
            writer.println("гонец");
        }
    }
    
    @AfterEach
    void tearDown() {
        new File(TEST_DICT_PATH).delete();
        testLogger.flush();
    }
    
    @Test
    void testLoadFromFile() throws IOException {
        WordleDictionary dict = WordleDictionaryLoader.loadFromFile(TEST_DICT_PATH, testLogger);
        
        assertNotNull(dict);
        assertEquals(3, dict.size());
        assertTrue(dict.containsWord("герой"));
        assertTrue(dict.containsWord("горец"));
        assertTrue(dict.containsWord("гонец"));
        assertFalse(dict.containsWord("кот"));
        assertFalse(dict.containsWord("длинное"));
    }
    
    @Test
    void testNormalizeWordWithYo() {
        assertEquals("еж", WordleDictionary.normalizeWord("ёж"));
        assertEquals("ежик", WordleDictionary.normalizeWord("ёжик"));
        assertEquals("елка", WordleDictionary.normalizeWord("ёлка"));
    }
    
    @Test
    void testLoadFromFileThrowsExceptionWhenFileNotFound() {
        assertThrows(IOException.class, () -> {
            WordleDictionaryLoader.loadFromFile("nonexistent_file_12345.txt", testLogger);
        });
    }
}
