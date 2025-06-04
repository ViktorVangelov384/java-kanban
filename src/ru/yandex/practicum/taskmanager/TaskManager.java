package ru.yandex.practicum.taskmanager;

import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.Subtask;
import ru.yandex.practicum.task.Task;

import java.util.*;

public interface TaskManager {
    List<Task> getAllTasks();

    void deleteAllTasks();

    Task getTaskById(int id);

    int createTask(Task task);

    void updateTask(Task task);

    void deleteTaskById(int id);

    List<Epic> getAllEpics();

    Epic getEpicById(int id);

    int createEpic(Epic epic);

    void updateEpic(Epic epic);

    void deleteAllEpics();

    void deleteEpicById(int id);

    List<Subtask> getAllSubtasks();

    Subtask getSubtaskById(int id);

    int createSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    void deleteSubtask(int id);

    void deleteAllSubtasks();

    List<Subtask> getSubtaskByEpicId(int epicId);

    List<Task> getHistory();
}
