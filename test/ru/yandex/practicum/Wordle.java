package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.WordNotInDictionaryException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Wordle {
    private static PrintWriter logger;
    private static final String DICTIONARY_PATH = "words_ru.txt";
    private static final String LOG_PATH = "logs/game.log";

    public static void main(String[] args) {
        try {
            new File("logs").mkdirs();
            logger = new PrintWriter(new OutputStreamWriter(
                    new FileOutputStream(LOG_PATH, true), StandardCharsets.UTF_8));

            logger.println("=== Новая игра ===");
            WordleDictionary dict = WordleDictionaryLoader.loadFromFile(DICTIONARY_PATH, logger);
            playGame(dict);

        } catch (Exception e) {
            System.err.println("Критическая ошибка: " + e.getMessage());
            if (logger != null) {
                logger.println("FATAL: " + e.getMessage());
                e.printStackTrace(logger);
                logger.close();
            }
        }
    }

    private static void playGame(WordleDictionary dictionary) {
        WordleGame game = new WordleGame(dictionary, logger);
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== WORDLE ===");
        System.out.println("Угадайте слово из 5 букв за 6 попыток.");
        System.out.println("+ буква на месте, ^ буква есть, - буквы нет.");
        System.out.println("Enter → подсказка\n");

        while (!game.isGameOver()) {
            System.out.println("Попыток: " + game.getRemainingAttempts());
            System.out.print("Ваше слово: ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                String hint = game.getHint();
                System.out.println(hint != null ? "Подсказка: " + hint.toUpperCase() : "Нет подходящих слов");
                continue;
            }

            try {
                GameResult result = game.makeGuess(input);
                System.out.println(game.getLastHint());

                if (result == GameResult.WIN) {
                    System.out.println("Победа! Слово: " + game.getSecretWord().toUpperCase());
                    break;
                } else if (result == GameResult.LOSE) {
                    System.out.println("Проигрыш! Было загадано: " + game.getSecretWord().toUpperCase());
                    break;
                }
            } catch (WordNotInDictionaryException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
        scanner.close();
        if (logger != null) logger.close();
    }
}
