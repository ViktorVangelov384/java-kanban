package ru.yandex.practicum.taskmanager;

import  org.junit.jupiter.api.Test;
import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.Subtask;
import ru.yandex.practicum.task.Task;
import ru.yandex.practicum.task.TaskStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryTaskManagerTest {
    private final TaskManager manager = Managers.getDefault();

    @Test
    public void shouldAddAndRetriveDifferentTaskTypes() {
        Task task = new Task("Задача", "Описание задачи", TaskStatus.NEW);
        Epic epic = new Epic("Эпик", "Описание эпика");
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", TaskStatus.NEW, 2);

        int taskId = manager.createTask(task);
        int epicId = manager.createEpic(epic);
        manager.createEpic(new Epic("Эпик", "Описание эпика"));
        int subtaskId = manager.createSubtask(subtask);

        assertEquals(task, manager.getTaskById(taskId));
        assertEquals(epic, manager.getEpicById(epicId));
        assertEquals(subtask, manager.getSubtaskById(subtaskId));
    }

}