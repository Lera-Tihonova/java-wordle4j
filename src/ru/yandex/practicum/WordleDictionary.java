package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.GameException;
import java.util.*;
import java.util.stream.Collectors;

public class WordleDictionary {
    private final Set<String> words;
    private final List<String> wordsList;

    public WordleDictionary(Set<String> words) {
        this.words = new HashSet<>(words);
        this.wordsList = new ArrayList<>(words);
    }

    public boolean containsWord(String word) {
        return words.contains(normalizeWord(word));
    }

    public String getRandomWord() {
        if (wordsList.isEmpty()) {
            throw new GameException("Словарь пуст");
        }
        return wordsList.get(new Random().nextInt(wordsList.size()));
    }

    public List<String> getPossibleWords(Set<Character> mustContain,
                                         Map<Integer, Character> exactMatches,
                                         Set<Character> notContain) {
        return wordsList.stream()
                .filter(word -> containsAllRequiredLetters(word, mustContain))
                .filter(word -> matchesExactPositions(word, exactMatches))
                .filter(word -> containsNoForbiddenLetters(word, notContain))
                .collect(Collectors.toList());
    }

    public static String normalizeWord(String word) {
        if (word == null) return "";
        return word.toLowerCase().trim().replace('ё', 'е');
    }

    public static String getHintPattern(String guess, String secret) {
        StringBuilder pattern = new StringBuilder();
        char[] guessChars = guess.toCharArray();
        char[] secretChars = secret.toCharArray();
        boolean[] used = new boolean[secret.length()];

        for (int i = 0; i < guessChars.length; i++) {
            if (guessChars[i] == secretChars[i]) {
                pattern.append('+');
                used[i] = true;
            } else {
                pattern.append(' ');
            }
        }

        for (int i = 0; i < guessChars.length; i++) {
            if (pattern.charAt(i) == '+') continue;
            boolean found = false;
            for (int j = 0; j < secretChars.length; j++) {
                if (!used[j] && guessChars[i] == secretChars[j]) {
                    found = true;
                    used[j] = true;
                    break;
                }
            }
            pattern.setCharAt(i, found ? '^' : '-');
        }
        return pattern.toString();
    }

    private boolean containsAllRequiredLetters(String word, Set<Character> mustContain) {
        if (mustContain == null || mustContain.isEmpty()) return true;
        for (char c : mustContain) {
            if (word.indexOf(c) == -1) return false;
        }
        return true;
    }

    private boolean matchesExactPositions(String word, Map<Integer, Character> exactMatches) {
        if (exactMatches == null || exactMatches.isEmpty()) return true;
        for (var entry : exactMatches.entrySet()) {
            int pos = entry.getKey();
            char expected = entry.getValue();
            if (pos >= word.length() || word.charAt(pos) != expected) return false;
        }
        return true;
    }

    private boolean containsNoForbiddenLetters(String word, Set<Character> notContain) {
        if (notContain == null || notContain.isEmpty()) return true;
        for (char c : notContain) {
            if (word.indexOf(c) != -1) return false;
        }
        return true;
    }

    public int size() {
        return words.size();
    }
}
