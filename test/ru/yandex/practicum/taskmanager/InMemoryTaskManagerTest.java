package ru.yandex.practicum.taskmanager;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.Subtask;
import ru.yandex.practicum.task.Task;
import ru.yandex.practicum.task.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {
    private final TaskManager manager = Managers.getDefault();

    @Test
    public void shouldAddAndRetriveDifferentTaskTypes() {
        Task task = new Task("Задача", "Описание задачи", TaskStatus.NEW);
        Epic epic = new Epic("Эпик", "Описание эпика");
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", TaskStatus.NEW, 2);

        int taskId = manager.createTask(task);
        int epicId = manager.createEpic(epic);
        int subtaskId = manager.createSubtask(subtask);

        assertTrue(manager.getTaskById(taskId) instanceof Task);
        assertTrue(manager.getEpicById(epicId) instanceof Epic);
        assertTrue(manager.getSubtaskById(subtaskId) instanceof Subtask);

        assertEquals(task.getName(), manager.getTaskById(taskId).getName());
        assertEquals(epic.getDescription(), manager.getEpicById(epicId).getDescription());
        assertEquals(subtask.getStatus(), manager.getSubtaskById(subtaskId).getStatus());
    }

    @Test
    public void shouldNotModifyHistoryWhenTaskUpdated() {
        Task task = new Task("Задача", "Описание", TaskStatus.NEW);
        int taskId = manager.createTask(task);

        Task taskFromManager = manager.getTaskById(taskId);
        taskFromManager.setStatus(TaskStatus.DONE);
        assertNotEquals(taskFromManager.getStatus(),
                manager.getTaskById(taskId).getStatus()
        );
    }

    @Test
    public void shouldCleanSubtasksWhenEpicRemoved() {
        Epic epic = new Epic("Эпик", "Описание");
        int epicId = manager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW, epicId);
        int subtaskId = manager.createSubtask(subtask);

        manager.deleteEpicById(epicId);
        assertNotNull(manager.getSubtaskByEpicId(subtaskId));
        assertFalse(manager.getHistory().contains(subtask));
    }

    @Test
    public void shouldPreventDataCorruptionTroughtSetters() {
        Task task = new Task("Задача", "Описание", TaskStatus.NEW);
        int taskId = manager.createTask(task);

        Task managedTask = manager.getTaskById(taskId);
        managedTask.setStatus(TaskStatus.DONE);

        Task newTask = manager.getTaskById(taskId);
        assertEquals(TaskStatus.NEW, newTask.getStatus());
    }

}