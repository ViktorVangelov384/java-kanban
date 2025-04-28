package ru.yandex.practicum.taskmanager;

import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.Subtask;
import ru.yandex.practicum.task.Task;

import java.util.*;
import java.util.ArrayList;



public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_HISTORY_SIZE = 10;
    private final Map<Integer, Task> history = new LinkedHashMap<>
            (MAX_HISTORY_SIZE, 0.75f, true) {

        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, Task> eldest) {
            return size() > MAX_HISTORY_SIZE;
        }
    };

    @Override
    public void add(Task task) {
        if (task == null) return;
        history.put(task.getId(), createCopy(task));
    }
    private Task createCopy(Task original) {
        if (original instanceof Epic epic) {

            Epic copy = new Epic(epic.getName(), epic.getDescription());
            copy.setId(epic.getId());
            copy.setStatus(epic.getStatus());
            return copy;
        }
        else if (original instanceof Subtask subtask) {
            Subtask copy = new Subtask(
                    subtask.getName(),
                    subtask.getDescription(),
                    subtask.getStatus(),
                    subtask.getEpicId()
            );
            copy.setId(subtask.getId());
            return copy;
        }
        else {
            Task copy = new Task(
                    original.getName(),
                    original.getDescription(),
                    original.getStatus()
            );
            copy.setId(original.getId());
            return copy;
        }
    }


    @Override
    public void remove(int id) {
         history.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history.values());
    }
}
