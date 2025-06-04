package ru.yandex.practicum.taskmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.Subtask;
import ru.yandex.practicum.task.Task;
import ru.yandex.practicum.task.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager = Managers.getDefaultHistory();

    @BeforeEach
    public void set() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void shouldPReserveTaskDataInHistory() {
        Task task = new Task("Задача", "Описание задачи", TaskStatus.NEW);
        task.setId(1);
        historyManager.add(task);

        task.setName("Новое название задачи");
        task.setDescription("Новое Описание");
        task.setStatus(TaskStatus.DONE);

        Task historialTask = historyManager.getHistory().get(0);
        assertEquals("Задача", historialTask.getName(), "Имя задaчи изменилось");
        assertEquals("Описание задачи", historialTask.getDescription(), "Описание изменилось");
        assertEquals(TaskStatus.NEW, historialTask.getStatus(), "Статус изменился");
    }

    @Test
    public void shouldNotAffectHistoryWhenOriginalTaskChange() {
        Task task = new Task( "Название", "Описание", TaskStatus.NEW);
        task.setId(1);
        historyManager.add(task);

        historyManager.add(task);
        task.setStatus(TaskStatus.DONE);

        assertNotEquals(task.getStatus(), historyManager.getHistory().get(0).getStatus(),
                "Изменение задачи не должно влият на историю");
    }

    @Test
    public void shouldCreateDeepCopyForEpicTasks() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        epic.setId(1);
        historyManager.add(epic);

        epic.setName("Имененный эпик");
        Epic historyEpic = (Epic) historyManager.getHistory().get(0);
        assertNotEquals(epic.getName(), historyEpic.getName());
        assertEquals("Эпик", historyEpic.getName());
    }

    @Test
    public void shouldCreateDeepCopyForSubtaskTasks() {
        Subtask subtask = new Subtask( "Подзадача", "Описание", TaskStatus.NEW, 1);
        subtask.setId(2);
        historyManager.add(subtask);

        subtask.setStatus(TaskStatus.DONE);
        Subtask historySubtask = (Subtask) (historyManager.getHistory().get(0));
        assertEquals(TaskStatus.NEW, historySubtask.getStatus());
    }

    @Test
    public void shouldMaintainOrderAfterMultipleAdds() {
        Task task1 = new Task( "Задача1", "Описание1", TaskStatus.NEW);
        Task task2 = new Task( "Задача2", "Опидание2", TaskStatus.NEW);
        task1.setId(1);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task2, history.get(0));
        assertEquals(task1, history.get(1));
    }

    @Test
    public void shouldRemoveTaskFromHistoryWhenDeleted() {
        Task task = new Task( "Задача", "Описание", TaskStatus.NEW);
        task.setId(1);
        historyManager.add(task);
        historyManager.remove(1);
        assertTrue(historyManager.getHistory().isEmpty(),
                "Проверка на удаление задач");
    }

    @Test
    public void shouldHandleEmptyHistoryCorrectly() {
        assertTrue(historyManager.getHistory().isEmpty(), "Для проверки работы с пустой историей");
        historyManager.remove(999);
    }

}