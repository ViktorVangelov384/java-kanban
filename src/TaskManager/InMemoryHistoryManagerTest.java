package taskmanager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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
        Task task = new Task("Название", "Описание", TaskStatus.NEW);
        task.setId(1);
        historyManager.add(task);

        historyManager.add(task);
        task.setStatus(TaskStatus.DONE);

        assertNotEquals(task.getStatus(), historyManager.getHistory().get(0).getStatus(),
                "Изменение задачи не должно влият на историю");
    }
}