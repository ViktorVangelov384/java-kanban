package ru.yandex.practicum.task;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.taskmanager.Managers;
import ru.yandex.practicum.taskmanager.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    @Test
    public void shouldNotAllowSubtaskToBeItsOwnEpic() {
        TaskManager manager = Managers.getDefault();


        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи",
                TaskStatus.NEW, 1);
        subtask.setId(1);


        assertThrows(IllegalArgumentException.class, () -> {
            manager.createSubtask(subtask);
        }, "Должно выбрасывать исключение для подзадачи с самоссылкой);");
        assertTrue(manager.getAllSubtasks().isEmpty(), "Неправильная подзача не должна добавляться");
    }

    @Test
    public void subtaskShouldBeEqualIfIdsAreEqual() {
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", TaskStatus.NEW, 1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", TaskStatus.DONE, 1);

        subtask1.setId(1);
        subtask2.setId(1);
        assertEquals(subtask1, subtask2, "Подзадачи с одинаковыми id должны быть равны");

    }
}