package ru.yandex.practicum.taskmanager;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.Subtask;
import ru.yandex.practicum.task.Task;
import ru.yandex.practicum.task.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    private final TaskManager manager = Managers.getDefault();

    @Override
    protected InMemoryTaskManager createManager() {
        return new InMemoryTaskManager();
    }

    @Test
    void shouldNotAllowSubtaskWithoutEpic() {
        assertThrows(IllegalArgumentException.class, () -> manager.createSubtask(new Subtask("Подзадача",
                "Описание", TaskStatus.NEW, 999)));
    }

    @Test
    void shouldNotAllowSubtaskToBeItsOwnEpic() {
        int epicId = manager.createEpic(new Epic("Эпик", "Описание"));
        Subtask subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW, epicId);
        subtask.setId(epicId);
        assertThrows(IllegalArgumentException.class, () -> manager.createSubtask(subtask));
    }

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

        assertEquals(TaskStatus.DONE, manager.getTaskById(taskId).getStatus());

        assertSame(taskFromManager, manager.getTaskById(taskId));

        Task updatedTask = new Task("Обновленная", "Новое описание", TaskStatus.IN_PROGRESS);
        updatedTask.setId(taskId);
        manager.updateTask(updatedTask);

        assertEquals(TaskStatus.IN_PROGRESS, manager.getTaskById(taskId).getStatus());

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
        TaskStatus originalStatus = managedTask.getStatus();

        Task newTask = manager.getTaskById(taskId);
        assertEquals(TaskStatus.NEW, newTask.getStatus());
    }

    @Test
    public void shouldRemoveTaskFromHistoryWhenDeletedById() {
        Task task = new Task("Задача", "Описание", TaskStatus.NEW);
        int taskId = manager.createTask(task);
        manager.getTaskById(taskId);
        assertTrue(manager.getHistory().contains(task));
        manager.deleteTaskById(taskId);
        assertFalse(manager.getHistory().contains(task));
    }

    @Test
    public void shouldRemoveAllTasksFromHistoryWhenDeleteAllTasks() {
        Task task1 = new Task("Задача1", "Описание", TaskStatus.NEW);
        Task task2 = new Task("Задача2", "Описание", TaskStatus.NEW);
        int taskId1 = manager.createTask(task1);
        int taskId2 = manager.createTask(task2);
        manager.getTaskById(taskId1);
        manager.getTaskById(taskId2);

        assertEquals(2, manager.getHistory().size(), "Должно быть 2 задачи");
        manager.deleteAllTasks();
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    public void shouldRemoveAllEpicsAndSubtasksFromHistoryWhenDeleteAllEpics() {
        Epic epic = new Epic("Эпик", "Описание");
        int epicId = manager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание", TaskStatus.NEW, epicId);
        int subtaskId = manager.createSubtask(subtask);
        manager.getEpicById(epicId);
        manager.getSubtaskById(subtaskId);
        assertEquals(2, manager.getHistory().size(), "1 эпик и 1 подзадача должны быть");
        manager.deleteAllEpics();
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    public void shouldRemoveAllSubtasksFromHistoryWhenDeleteAllSubtasks() {
        Epic epic = new Epic("Эпик", "Описание");
        int epicId = manager.createEpic(epic);
        Subtask subtask1 = new Subtask(" Подзадача", "Описание", TaskStatus.NEW, epicId);
        Subtask subtask2 = new Subtask("Подзадача", "Описание", TaskStatus.NEW, epicId);
        int subtask1Id = manager.createSubtask(subtask1);
        int subtask2Id = manager.createSubtask(subtask2);
        manager.getSubtaskById(subtask1Id);
        manager.getSubtaskById(subtask2Id);
        assertEquals(2, manager.getHistory().size());
        manager.deleteAllSubtasks();
        assertTrue(manager.getHistory().isEmpty());
    }
}