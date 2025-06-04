package ru.yandex.practicum.taskmanager;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.task.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {

        tempFile = File.createTempFile("tasks", ".csv");
        tempFile.deleteOnExit();
        manager = new FileBackedTaskManager(tempFile);
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
    void shouldSaveMultipleTasks() {
        File tempFile = new File("test_tasks.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Task task1 = new Task("Задача", "Описание", TaskStatus.NEW);
        Epic epic1 = new Epic("эпик", "Описание");
        Subtask subtask1 = new Subtask("Подзадача", "Описание", TaskStatus.DONE, 2);

        manager.createTask(task1);
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        List<Task> allTasks = loadedManager.getAllTasks();
        assertEquals(3, loadedManager.getAllTasks().size(), "Должны загрузиться все задачи");

        assertNotNull(loadedManager.getTaskById(1), "задача не загрузилась");
        assertNotNull(loadedManager.getEpicById(2), "эпик не загрузился");
        assertNotNull(loadedManager.getSubtaskById(3), "Подзадача не загрузилась");

        Subtask loadedSubtask = loadedManager.getSubtaskById(3);
        assertEquals(2, loadedSubtask.getEpicId(), "Не сохранилась связь подзадачи с эпиком");


    }
}

