package ru.yandex.practicum.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.TaskManager;
import ru.yandex.practicum.taskmanager.Managers;

import static org.junit.jupiter.api.Assertions.*;

class EpicStatusTest {
    private TaskManager manager;
    private Epic epic;

    @BeforeEach
    void setUp() {
        manager = Managers.getDefault();
        epic = new Epic("Эпик", "Описание");
        manager.createEpic(epic);
    }

    @Test
    void shouldBeNewWhenNoSubtasks() {
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void shouldBeNewWhenAllSubtasksNew() {
        Subtask subtask1 = new Subtask("Подзадача1", "Описание", TaskStatus.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача2", "Описание", TaskStatus.NEW, epic.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void shouldBeDoneWhenAllSubtasksDone() {
        Subtask subtask1 = new Subtask("Подзадача1", "Описание", TaskStatus.DONE, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача2", "Описание", TaskStatus.DONE, epic.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    void shouldBeInProgressWhenSubtasksNewAndDone() {
        Subtask subtask1 = new Subtask("Подзадача1", "Описание", TaskStatus.DONE, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача2", "Описание", TaskStatus.NEW, epic.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void shouldBeInProgressWhenAnySubtaskInProgress() {
        Subtask subtask1 = new Subtask("Подзадача1", "Описание", TaskStatus.IN_PROGRESS, epic.getId());
        Subtask subtask2 = new Subtask("Подзадача2", "Описание", TaskStatus.DONE, epic.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }
}
