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
        FileBackedTaskManager manager1 = new FileBackedTaskManager(tempFile);


        int taskId1 = manager1.createTask(new Task("Задача1", "Описание1", TaskStatus.NEW));
        int taskId2 = manager1.createTask(new Task("Задача2", "Описание2", TaskStatus.IN_PROGRESS));

        int epicId = manager1.createEpic(new Epic("Эпик", "Описание"));
        int subtaskId = manager1.createSubtask(new Subtask("Подзадача", "Описание",
                TaskStatus.DONE, epicId));

        assertTrue(tempFile.exists());
        assertTrue(tempFile.length() > 0);

        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(tempFile);


        assertNotNull(manager2.getTaskById(taskId1), "Задача1 не загрузилась");
        assertNotNull(manager2.getTaskById(taskId2), "Задача2 не загрузилась");
        assertNotNull(manager2.getEpicById(epicId), "Эпик не загрузился");
        assertNotNull(manager2.getSubtaskById(subtaskId), "Подзадача не загрузилась");


        compareTasks(manager1.getTaskById(taskId1), manager2.getTaskById(taskId1));
        compareTasks(manager1.getTaskById(taskId2), manager2.getTaskById(taskId2));
        compareEpics(manager1.getEpicById(epicId), manager2.getEpicById(epicId));
        compareSubtasks(manager1.getSubtaskById(subtaskId), manager2.getSubtaskById(subtaskId));


        Epic loadedEpic = manager2.getEpicById(epicId);
        assertTrue(loadedEpic.getSubtaskIds().contains(subtaskId));
    }

    private void compareTasks(Task expected, Task actual) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());

    }

    private void compareEpics(Epic expected, Epic actual) {
        compareTasks(expected, actual);
        assertEquals(expected.getSubtaskIds().size(), actual.getSubtaskIds().size());
        assertTrue(actual.getSubtaskIds().containsAll(expected.getSubtaskIds()));

    }

    private void compareSubtasks(Subtask expected, Subtask actual) {
        compareTasks(expected, actual);
        assertEquals(expected.getEpicId(), actual.getEpicId());
    }

}

