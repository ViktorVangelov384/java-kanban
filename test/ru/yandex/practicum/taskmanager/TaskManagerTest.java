package ru.yandex.practicum.taskmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.Subtask;
import ru.yandex.practicum.task.Task;
import ru.yandex.practicum.task.TaskStatus;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;
    protected Task task;
    protected Epic epic;
    protected Subtask subtask;

    protected abstract T createManager();

    @BeforeEach
    void setUp() throws IOException {
        manager = createManager();
        task = new Task("Задача", "Описание", TaskStatus.NEW);
        epic = new Epic("Эпик", "Описание");
        subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW, 2);
    }

    @Test
    void shouldCreateAndGetTask() {
        int taskId = manager.createTask(task);
        Task savedTask = manager.getTaskById(taskId);

        assertNotNull(savedTask);
        assertEquals(task.getName(), savedTask.getName());
        assertEquals(task.getDescription(), savedTask.getDescription());
        assertEquals(task.getStatus(), savedTask.getStatus());
    }

    @Test
    void shouldCreateAndGetEpic() {
        int epicId = manager.createEpic(epic);
        Epic savedEpic = manager.getEpicById(epicId);

        assertNotNull(savedEpic);
        assertEquals(epic.getName(), savedEpic.getName());
        assertEquals(epic.getDescription(), savedEpic.getDescription());
        assertEquals(TaskStatus.NEW, savedEpic.getStatus());
    }

    @Test
    void shouldCreateAndGetSubtask() {
        int epicId = manager.createEpic(epic);
        subtask.setEpicId(epicId);
        int subtaskId = manager.createSubtask(subtask);
        Subtask savedSubtask = manager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask);
        assertEquals(subtask.getName(), savedSubtask.getName());
        assertEquals(subtask.getDescription(), savedSubtask.getDescription());
        assertEquals(subtask.getStatus(), savedSubtask.getStatus());
        assertEquals(epicId, savedSubtask.getEpicId());
    }

    @Test
    void shouldUpdateTaskStatus() {
        int taskId = manager.createTask(task);
        Task updatedTask = new Task("Обновленная задача", "Описание", TaskStatus.DONE);
        updatedTask.setId(taskId);
        manager.updateTask(updatedTask);
        assertEquals(TaskStatus.DONE, manager.getTaskById(taskId).getStatus());
    }


    @Test
    void shouldDeleteTask() {
        int taskId = manager.createTask(task);
        manager.deleteTaskById(taskId);
        assertNull(manager.getTaskById(taskId));
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void shouldDeleteEpicWithSubtask() {
        Epic epic = new Epic("Эпик", "Описание");
        int epicId = manager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW, epicId);
        subtask.setEpicId(epicId);
        int subtaskId = manager.createSubtask(subtask);
        manager.deleteEpicById(epicId);
        assertNull(manager.getEpicById(epicId));
        assertNull(manager.getSubtaskById(subtaskId));
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void shouldCheckTimeOverlaps() {
        Task task1 = new Task("Задача1", "Описание", TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofMinutes(30));
        Task task2 = new Task("Задача2", "Описание", TaskStatus.NEW,
                LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(30));
        manager.createTask(task1);
        assertThrows(IllegalStateException.class, () -> manager.createTask(task2));
    }

    @Test
    void shouldPrioritizeTasks() {
        Task task1 = new Task("задача1", "Описание", TaskStatus.NEW,
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(10));
        Task task2 = new Task("Задача2", "Описание", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(10));
        manager.createTask(task1);
        manager.createTask(task2);
        List<Task> prioritized = manager.getPrioritizedTasks();
        assertFalse(prioritized.isEmpty(), "Список приоритетов не должен быть пустым");

        assertEquals(2, prioritized.size(), "Должно быть 2 задачи в списке приоритетов");
        assertEquals(task2, prioritized.get(0), "Первой должна быть задача с более ранним временем");
        assertEquals(task1, prioritized.get(1), "Второй должна быть задача с более поздним временем");
    }

    @Test
    void shouldNotAllowOverlappingTasksWithSameStartTime() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofMinutes(30);

        Task task1 = new Task("Задача1", "Описание", TaskStatus.NEW, startTime, duration);
        Task task2 = new Task("Задача2", "Описание", TaskStatus.NEW, startTime, duration);
        manager.createTask(task1);
        assertThrows(IllegalStateException.class, () -> manager.createTask(task2));
    }

    @Test
    void shouldAllowNonOverlappingTasks() {
        LocalDateTime startTime1 = LocalDateTime.now();
        LocalDateTime startTime2 = startTime1.plusHours(1);
        Duration duration = Duration.ofMinutes(30);

        Task task1 = new Task("Задача1", "Описание", TaskStatus.NEW, startTime1, duration);
        Task task2 = new Task("Задача2", "Описание", TaskStatus.NEW, startTime2, duration);

        assertDoesNotThrow(() -> {
            manager.createTask(task1);
            manager.createTask(task2);
        });
    }

    @Test
    void shouldReturnEmptyListWhenNoTasksWithTime() {
        Task task = new Task("Задача", "Описание", TaskStatus.NEW);
        manager.createTask(task);
        assertTrue(manager.getPrioritizedTasks().isEmpty());
    }

    @Test
    void shouldSortTasksByStartTime() {
        Task task1 = new Task("ЗАдача", "Описание", TaskStatus.NEW,
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(30));
        Task task2 = new Task("Задача", "Описание", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30));
        manager.createTask(task1);
        manager.createTask(task2);
        List<Task> prioritized = manager.getPrioritizedTasks();
        assertEquals(task2, prioritized.get(0));
        assertEquals(task1, prioritized.get(1));
    }
}
