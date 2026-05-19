package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.WordNotInDictionaryException;
import java.io.PrintWriter;
import java.util.*;

public class WordleGame {
    private static final int WORD_LENGTH = 5;
    private static final int MAX_ATTEMPTS = 6;

    private final WordleDictionary dictionary;
    private final String secretWord;
    private int remainingAttempts;
    private boolean isGameOver;
    private final Set<Character> knownLetters;
    private final Set<Character> notInWord;
    private final Map<Integer, Character> exactMatches;
    private final List<String> previousHints;
    private String lastHint;

    public WordleGame(WordleDictionary dictionary, PrintWriter logger) {
        this.dictionary = dictionary;
        this.secretWord = dictionary.getRandomWord();
        this.remainingAttempts = MAX_ATTEMPTS;
        this.isGameOver = false;
        this.knownLetters = new HashSet<>();
        this.notInWord = new HashSet<>();
        this.exactMatches = new HashMap<>();
        this.previousHints = new ArrayList<>();
        this.lastHint = "";

        if (logger != null) {
            logger.println("Игра создана. Слово загадано.");
            logger.flush();
        }
    }

    public GameResult makeGuess(String guess) throws WordNotInDictionaryException {
        if (isGameOver) {
            throw new IllegalStateException("Игра завершена");
        }

        String normalized = WordleDictionary.normalizeWord(guess);

        if (normalized.length() != WORD_LENGTH) {
            throw new WordNotInDictionaryException("Слово должно быть из " + WORD_LENGTH + " букв");
        }
        if (!dictionary.containsWord(normalized)) {
            throw new WordNotInDictionaryException("Слова нет в словаре");
        }

        lastHint = WordleDictionary.getHintPattern(normalized, secretWord);

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

    public String getHint() {
        List<String> possible = dictionary.getPossibleWords(knownLetters, exactMatches, notInWord);
        possible.removeAll(previousHints);
        if (possible.isEmpty()) {
            return null;
        }
        String hintWord = possible.get(new Random().nextInt(possible.size()));
        previousHints.add(hintWord);
        return hintWord;
    }

    public String getLastHint() {
        return lastHint;
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
