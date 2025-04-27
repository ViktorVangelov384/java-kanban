package task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EpicTest {
    @Test
    public void epicShouldNotAddItselfAsSubtask() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        epic.setId(1);

        epic.addSubtaskId(epic.getId());
        assertTrue(epic.getSubtaskIds().isEmpty(), "Эпик не должен содержать себя в списке подзадач.");
    }
}