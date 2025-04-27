package taskmanager;

import task.Task;

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
        Task taskCopy = new Task(task.getName(), task.getDescription(), task.getStatus());
        taskCopy.setId(task.getId());
        history.put(taskCopy.getId(), taskCopy);
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
