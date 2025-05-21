package ru.yandex.practicum.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EpicTest {
    @Test
    public void epicShouldNotAddItselfAsSubtask() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        epic.setId(1);

        epic.addSubtaskId(epic.getId());
        assertTrue(epic.getSubtaskIds().isEmpty(), "Эпик не должен содержать себя в списке подзадач.");
    }

    @Test
    public void epicsShouldBeEqualIfIdsEqual() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        epic1.setId(1);
        epic2.setId(1);

        assertEquals(epic1, epic2, "Эпики с одинаковым id должны быть равны");
    }
}