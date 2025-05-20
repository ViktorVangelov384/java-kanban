package ru.yandex.practicum.taskmanager;

import ru.yandex.practicum.task.Task;

import java.util.*;

public interface HistoryManager {
    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}
