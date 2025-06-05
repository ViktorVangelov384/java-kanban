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
    void shouldSaveMultipleTasks() throws IOException {
        File tempFile = File.createTempFile("tasks", ".csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Task task1 = new Task("Задача", "Описание", TaskStatus.NEW);
        Epic epic1 = new Epic("эпик", "Описание");
        Subtask subtask1 = new Subtask("Подзадача", "Описание", TaskStatus.DONE, 2);

        int taskId = manager.createTask(task1);
        int epicId = manager.createEpic(epic1);
        int subtaskId = manager.createSubtask(subtask1);


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
    }
}

