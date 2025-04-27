package task;

import org.junit.jupiter.api.Test;
import taskmanager.Managers;
import taskmanager.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    @Test
    public void shouldNotAllowSubtaskToBeItsOwnEpic() {
        TaskManager manager = Managers.getDefault();

        Epic epic = new Epic("Эпик", "Описаноие эпика");
        int epicId = manager.createEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи",
                task.TaskStatus.NEW, 1);
        subtask.setId(1);

        Integer subtaskId = manager.createSubtask(subtask);

        assertNull(subtaskId, "Должно возвращать null для подзадачи с самоссылкой");
        assertTrue(manager.getSubtaskByEpicId(epicId).isEmpty(), "Неправильная подзача не должна добавляться");
    }
}