package task;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    public void testTaskById() {
        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        task1.setId(1);

        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.DONE);
        task2.setId(1);
        assertEquals(task1, task2, "Задачи с одинаковым ID должны быть равны.");
    }
    @Test
    public void testTaskNoEqualityDifferentIds() {
        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        task1.setId(1);
        Task task2 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        task2.setId(2);

        assertNotEquals(task1, task2, "задачи с разными ID не должны быть равны");
    }
}