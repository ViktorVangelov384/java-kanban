package ru.yandex.practicum.taskmanager;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.task.Task;
import ru.yandex.practicum.task.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    public void getDefaultShouldReturnInitializedTaskManager() {
        TaskManager manager = Managers.getDefault();
        assertNotNull(manager, "Менеджер задач не должен быть null");
        int taskId = manager.createTask(new Task( "Задача", "Описание",
                TaskStatus.NEW));
        assertTrue(taskId > 0, "Менеджер должен корректно создавать задачи");
        Task task = manager.getTaskById(taskId);
        assertNotNull(task, "Менеджер должен возвращать задачу");
    }

    @Test
    public void getDefaultHistoryShouldReturnInitializedHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager, "Менеджер истории нельзя быть null");
        Task task = new Task( "Задача", "Описание", TaskStatus.NEW);
        task.setId(1);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertFalse(history.isEmpty(), "Менеджер должен сохранять задачи");
        assertEquals(1, history.size(), "Должна быть одна задача");
    }
}