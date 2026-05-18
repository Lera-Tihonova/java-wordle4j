package ru.yandex.practicum.exceptions;

public class WordNotInDictionaryException extends Exception {
    public WordNotInDictionaryException(String message) {
        super(message);
    }
}
