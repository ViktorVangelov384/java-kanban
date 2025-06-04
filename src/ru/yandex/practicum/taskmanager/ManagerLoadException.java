package ru.yandex.practicum.taskmanager;

public class ManagerLoadException extends RuntimeException {
    public ManagerLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
