package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.WordNotInDictionaryException;
import java.io.PrintWriter;
import java.util.*;

public class WordleGame {
    private final WordleDictionary dictionary;
    private final String secretWord;
    private int remainingAttempts;
    private final List<String> attempts;
    private final List<String> hints;
    private boolean isGameOver;
    private final Set<Character> knownLetters;
    private final Set<Character> notInWord;
    private final Map<Integer, Character> exactMatches;
    private final List<String> previousHints;

    public WordleGame(WordleDictionary dictionary, PrintWriter logger) {
        this.dictionary = dictionary;
        this.secretWord = dictionary.getRandomWord();
        this.remainingAttempts = 6;
        this.attempts = new ArrayList<>();
        this.hints = new ArrayList<>();
        this.isGameOver = false;
        this.knownLetters = new HashSet<>();
        this.notInWord = new HashSet<>();
        this.exactMatches = new HashMap<>();
        this.previousHints = new ArrayList<>();

        if (logger != null) {
            logger.println("Игра создана. Загадано: " + secretWord);
            logger.flush();
        }
    }

    public GameResult makeGuess(String guess) throws WordNotInDictionaryException {
        if (isGameOver) {
            throw new IllegalStateException("Игра завершена");
        }

        String normalized = WordleDictionary.normalizeWord(guess);

        if (normalized.length() != 5) {
            throw new WordNotInDictionaryException("Слово должно быть из 5 букв");
        }
        if (!dictionary.containsWord(normalized)) {
            throw new WordNotInDictionaryException("Слова нет в словаре");
        }

        attempts.add(normalized);
        String hint = WordleDictionary.getHintPattern(normalized, secretWord);
        hints.add(hint);
        updateKnowledge(normalized, hint);

        if (normalized.equals(secretWord)) {
            isGameOver = true;
            return GameResult.WIN;
        }

        remainingAttempts--;
        if (remainingAttempts == 0) {
            isGameOver = true;
            return GameResult.LOSE;
        }

        return GameResult.CONTINUE;
    }

    private void updateKnowledge(String guess, String hint) {
        char[] g = guess.toCharArray();
        char[] h = hint.toCharArray();

        for (int i = 0; i < g.length; i++) {
            if (h[i] == '+') {
                knownLetters.add(g[i]);
                exactMatches.put(i, g[i]);
            } else if (h[i] == '^') {
                knownLetters.add(g[i]);
            } else if (h[i] == '-' && !knownLetters.contains(g[i])) {
                notInWord.add(g[i]);
            }
        }
    }

    public String getHint() {
        List<String> possible = dictionary.getPossibleWords(knownLetters, exactMatches, notInWord);
        possible.removeAll(previousHints);
        possible.removeAll(attempts);
        if (possible.isEmpty()) {
            return null;
        }
        String hintWord = possible.get(new Random().nextInt(possible.size()));
        previousHints.add(hintWord);
        return hintWord;
    }

    public String getLastHint() {
        if (hints.isEmpty()) {
            return "";
        }
        return hints.get(hints.size() - 1);
    }

    public String getSecretWord() {
        return secretWord;
    }

    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    public boolean isGameOver() {
        return isGameOver;
    }
}
