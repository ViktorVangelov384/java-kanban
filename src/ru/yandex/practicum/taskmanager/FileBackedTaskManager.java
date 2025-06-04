package ru.yandex.practicum.taskmanager;

import ru.yandex.practicum.task.*;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private static final String HEADER = "id,type,name,status,description,epic";

    public FileBackedTaskManager(File file) {
        this.file = file;
    }


    private void addTaskToManager(Task task) {
        if (task.getType() == TaskType.EPIC) {
            epics.put(task.getId(), (Epic) task);
        } else if (task.getType() == TaskType.SUBTASK) {
            subtasks.put(task.getId(), (Subtask) task);
        } else {
            tasks.put(task.getId(), task);
        }
    }

    private void updateEpicForSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            List<Subtask> epicSubtasks = getSubtaskByEpicId(epic.getId());
            epic.addSubtaskId(subtask.getId());

        }
    }


    @Override
    public int createTask(Task task) {
        int id = super.createTask(task);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public int createEpic(Epic epic) {
        int id = super.createEpic(epic);
        save();
        return id;

    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public int createSubtask(Subtask subtask) {
        int id = super.createSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(HEADER + "\n");

            for (Task task : getAllTasks()) {
                writer.write(taskToString(task) + "\n");
            }
            for (Epic epic : getAllEpics()) {
                writer.write(taskToString(epic) + "\n");

            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(taskToString(subtask) + "\n");

            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл", e);
        }
    }


    private String taskToString(Task task) {
        String epicId = (task instanceof Subtask) ? String.valueOf(((Subtask) task).getEpicId()) : "";
        return String.join(",",
                String.valueOf(task.getId()),
                task.getType().name(),
                task.getName(),
                task.getStatus().name(),
                task.getDescription(),
                epicId
        );
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            String content = Files.readString(file.toPath());
            String[] lines = content.split("\n");

            if (lines.length <= 1) return manager;

            int maxId = 0;
            for (int i = 1; i < lines.length; i++) {
                Task task = fromString(lines[i]);
                if (task != null) {
                    if (task.getId() > maxId) {
                        maxId = task.getId();
                    }
                    manager.addTaskToManager(task);

                }
            }
            manager.nextId = maxId + 1;

            for (Subtask subtask : new ArrayList<>(manager.subtasks.values())) {
                manager.updateEpicForSubtask(subtask);
            }
            for (Epic epic : manager.epics.values()) {
                manager.updateEpicStatus(epic.getId());
            }

        } catch (IOException e) {
            throw new ManagerLoadException("ошибка загрузки", e);
        }
        return manager;
    }

    private static Task fromString(String value) {
        if (value == null || value.isBlank()) return null;

        String[] parts = value.split(",");
        if (parts.length < 6) return null;

        try {
            int id = Integer.parseInt(parts[0]);
            TaskType type = TaskType.valueOf(parts[1].trim());
            String name = parts[2];
            TaskStatus status = TaskStatus.valueOf(parts[3]);
            String description = parts[4];
            String epicIdStr = parts[5];

            if (name.isEmpty() || description.isEmpty()) return null;

            Task task;
            switch (type) {
                case TASK:
                    task = new Task( name, description, status);
                    break;
                case EPIC:
                    Epic epic = new Epic( name, description);
                    epic.setStatus(status);
                    task = epic;
                    break;
                case SUBTASK:
                    if (epicIdStr.isEmpty()) {
                        return null;
                    }
                    if (epicIdStr.isEmpty()) return null;
                    int epicId = Integer.parseInt(epicIdStr);
                    task =  new Subtask(name, description, status, epicId);
                    break;
                default:
                    return null;
            }
            task.setId(id);
            return task;

        } catch (IllegalArgumentException e) {
            return null;
        }

    }

}
