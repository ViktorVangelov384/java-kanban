package ru.yandex.practicum.taskmanager;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.exceptions.ManagerLoadException;
import ru.yandex.practicum.task.*;

import java.io.*;
import java.nio.file.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {

        tempFile = File.createTempFile("tasks", ".csv");
        tempFile.deleteOnExit();
        manager = createManager();

        task = new Task("Задача", "Описание", TaskStatus.NEW);
        epic = new Epic("Эпик", "Описание");
        subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW, 1);
    }

    @Override
    protected FileBackedTaskManager createManager() {
        return new FileBackedTaskManager(tempFile);
    }

    @Test
    void shouldSaveAndLoadEmptyManager() {
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(loaded.getAllTasks().isEmpty());
        assertTrue(loaded.getAllEpics().isEmpty());
        assertTrue(loaded.getAllSubtasks().isEmpty());
    }

    @Test
    void shouldSaveAndLoadTasks() {
        int taskId = manager.createTask(new Task("Задача", "Описание", TaskStatus.NEW));
        int epicId = manager.createEpic(new Epic("Эпик", "Описание"));
        int subtaskId = manager.createSubtask(new Subtask("Подзадача", "Описание", TaskStatus.NEW, epicId));

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertNotNull(loaded.getTaskById(taskId));
        assertNotNull(loaded.getEpicById(epicId));
        assertNotNull(loaded.getSubtaskById(subtaskId));
    }

    @Test
    void shouldThrowWhenLoadingInvalidFile() {
        File invalidFile = new File("nonexistent.csv");
        assertThrows(ManagerLoadException.class, () -> FileBackedTaskManager.loadFromFile(invalidFile));
    }

    @Test
    void saveTasksToFile() throws IOException {
        manager.createTask(new Task("Задача", "Описание", TaskStatus.NEW));
        manager.createEpic(new Epic("эпик", "Описание"));
        String content = Files.readString(tempFile.toPath());
        assertTrue(content.contains("Задача"));
        assertTrue(content.contains("эпик"));

    }


    @Test
    void shouldLoadEmptyFile() {
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loaded.getAllTasks().isEmpty());
        assertTrue(loaded.getAllEpics().isEmpty());
        assertTrue(loaded.getAllSubtasks().isEmpty());
    }

    @Test
    void shouldDeleteTask() {
        int taskId = manager.createTask(new Task("Задача", "Описание", TaskStatus.NEW));
        manager.deleteTaskById(taskId);
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
        assertNull(loaded.getTaskById(taskId));
    }

    @Test
    void shouldSaveMultipleTasks() throws IOException {
        File tempFile = File.createTempFile("tasks", ".csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Task task1 = new Task("Задача", "Описание", TaskStatus.NEW);
        Epic epic1 = new Epic("эпик", "Описание");
        Subtask subtask1 = new Subtask("Подзадача", "Описание", TaskStatus.DONE, 2);

        int taskId = manager.createTask(task1);
        int epicId = manager.createEpic(epic1);
        int subtaskId = manager.createSubtask(subtask1);

        List<Task> originalPrioritized = manager.getPrioritizedTasks();


        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getAllTasks().size());
        assertEquals(1, loadedManager.getAllEpics().size());
        assertEquals(1, loadedManager.getAllSubtasks().size());


        Task loadedTask = loadedManager.getTaskById(taskId);
        assertNotNull(loadedTask);
        assertEquals(task1.getId(), loadedTask.getId(), "ID в Task не совпадает");
        assertEquals(task1.getName(), loadedTask.getName(), "название не совпадает");
        assertEquals(task1.getDescription(), loadedTask.getDescription(), "описание не совпадает");
        assertEquals(task1.getStatus(), loadedTask.getStatus(), "статус не совпадает");
        assertEquals(task1.getType(), loadedTask.getType());


        Epic loadedEpic = loadedManager.getEpicById(epicId);
        assertNotNull(loadedEpic);
        assertEquals(epic1.getId(), loadedEpic.getId(), "ID не совпадает");
        assertEquals(epic1.getName(), loadedEpic.getName(), "название не совпадает");
        assertEquals(epic1.getDescription(), loadedEpic.getDescription(), "описание не совпадает");
        assertEquals(epic1.getStatus(), loadedEpic.getStatus(), "статус не совпадает");
        assertEquals(epic1.getType(), loadedEpic.getType(), "тип не совпадает");

        Subtask loadedSubtask = loadedManager.getSubtaskById(subtaskId);
        assertNotNull(loadedSubtask);
        assertEquals(subtask1.getId(), loadedSubtask.getId(), "ID не совпадает");
        assertEquals(subtask1.getName(), loadedSubtask.getName(), "название не совпадает");
        assertEquals(subtask1.getDescription(), loadedSubtask.getDescription(), "описание не совпадает");
        assertEquals(subtask1.getStatus(), loadedSubtask.getStatus(), "статус не совпадает");
        assertEquals(subtask1.getType(), loadedSubtask.getType());
        assertEquals(subtask1.getEpicId(), loadedSubtask.getEpicId(), "ID эпика в подзадаче не совпадает");

        List<Integer> subtaskIdsInEpic = loadedEpic.getSubtaskIds();
        assertFalse(subtaskIdsInEpic.isEmpty());
        assertEquals(subtaskId, subtaskIdsInEpic.get(0));

        assertEquals(task1.getStartTime(), loadedTask.getStartTime());
        assertEquals(task1.getDuration(), loadedTask.getDuration());
        assertEquals(task1.getEndTime(), loadedTask.getEndTime());


        LocalDateTime now = LocalDateTime.now();
        Task taskWithTime = new Task("Задача2", "Описание", TaskStatus.NEW,
                now, Duration.ofMinutes(30));
        Subtask subtaskWithTime = new Subtask("Подзадача", "Описание", TaskStatus.DONE, epicId,
                now.plusHours(1), Duration.ofHours(1));
        manager.createTask(taskWithTime);
        manager.createSubtask(subtaskWithTime);

        manager.save();
        loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> prioritized = loadedManager.getPrioritizedTasks();
        assertEquals(2, prioritized.size());
        assertTrue(prioritized.contains(taskWithTime));
        assertTrue(prioritized.contains(subtaskWithTime));
        assertEquals(taskWithTime, prioritized.get(0));
    }

}