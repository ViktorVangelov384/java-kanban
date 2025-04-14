package TaskManager;

import task.*;

import java.util.*;

public class TaskManager {
    private int nextTaskId = 1;
    private int nextEpicId = 1;
    private int nextSubtaskId = 1;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }
    public void deleteAllTasks() {
        tasks.clear();
    }
    public Task getTaskById(int id) {
        return tasks.get(id);
    }
    public Task createTask(Task task) {
        task.setId(nextTaskId++);
        tasks.put(task.getId(), task);
        return task;
    }
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }
    public void deleteTaskById(int id){
        tasks.remove(id);
    }
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }
    public Epic getEpicById(int id) {
        return epics.get(id);
    }
    public Epic createEpic(Epic epic) {
        epic.setId(nextEpicId++);
        epics.put(epic.getId(), epic);
        return  epic;
    }
    public void updateEpic(Epic epic) {
        if (epic == null || !epics.containsKey(epic.getId())) return;
        Epic savedEpic = epics.get(epic.getId());
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
        updateEpicStatus(epic.getId());
    }
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                subtasks.remove(subtaskId);
            }
        }
    }
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }
    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }
    public Subtask createSubtask(Subtask subtask) {
        subtask.setId(nextSubtaskId++);
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(subtask.getId());
            updateEpicStatus(epic.getId());
        }
        return subtask;
    }
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
        }
    }
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic.getId());
            }
        }
    }
    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            epic.setStatus(TaskStatus.NEW);
        }
        subtasks.clear();
    }
    public List<Subtask> getSubtaskByEpicId(int epicId) {
        List<Subtask> result = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubtaskIds()) {
                result.add(subtasks.get(subtaskId));
            }
        }
        return result;
    }
    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null || epic.getSubtaskIds().isEmpty()) {
            if (epic != null) {
                epic.getStatus();
            }
            return;
        }
        boolean hasNew = false;
        boolean hasDone = false;
        boolean hasInProgress = false;

        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) {
                TaskStatus status = subtask.getStatus();
                hasNew = hasNew || status == TaskStatus.NEW;
                hasDone = hasDone || status == TaskStatus.DONE;
                hasInProgress = hasInProgress || status == TaskStatus.IN_PROGRESS;
            }
        }
        if (hasDone && !hasInProgress && hasNew) {
            epic.setStatus(TaskStatus.DONE);
        } else if (hasInProgress && !hasDone && !hasNew) {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        } else {
            epic.setStatus(TaskStatus.NEW);
        }
    }
}