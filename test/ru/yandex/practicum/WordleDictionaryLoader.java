package ru.yandex.practicum;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WordleDictionaryLoader {

    public static WordleDictionary loadFromFile(String filePath, PrintWriter logger) throws IOException {
        Set<String> validWords = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String normalized = WordleDictionary.normalizeWord(line);
                if (normalized.length() == 5 && normalized.matches("[а-яё]+")) {
                    validWords.add(normalized);
                }
            }
        }

        if (logger != null) {
            logger.println("Загружено слов из словаря: " + validWords.size());
            logger.flush();
        }

        if (validWords.isEmpty()) {
            throw new IOException("Словарь не содержит слов из 5 букв");
        }

        return new WordleDictionary(validWords);
    }
}
