package ru.yandex.practicum.taskmanager;


import ru.yandex.practicum.exceptions.ManagerLoadException;
import ru.yandex.practicum.exceptions.ManagerSaveException;
import ru.yandex.practicum.task.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;


public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private static final String HEADER = "id,type,name,status,description,startTime," +
            "duration,epic";

    public FileBackedTaskManager(File file) {
        this.file = file;
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

    protected void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(HEADER + "\n");

            for (Task task : tasks.values()) {
                writer.write(taskToString(task) + "\n");
            }
            for (Epic epic : epics.values()) {
                writer.write(taskToString(epic) + "\n");

            }
            for (Subtask subtask : subtasks.values()) {
                writer.write(taskToString(subtask) + "\n");

            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл", e);
        }

    }


    private String taskToString(Task task) {
        String startTimeStr = task.getStartTime() != null ? task.getStartTime().toString() : "null";
        String durationStr = task.getDuration() != null ? String.valueOf(task.getDuration().toMinutes()) : "0";
        String epicId = (task instanceof Subtask) ? String.valueOf(((Subtask) task).getEpicId()) : "";
        return String.join(",",
                String.valueOf(task.getId()),
                task.getType().name(),
                task.getName(),
                task.getStatus().name(),
                task.getDescription(),
                startTimeStr,
                durationStr,
                epicId
        );
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        int maxId = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {

                Task task = fromString(line);
                if (task != null) {
                    if (task.getId() > maxId) {
                        maxId = task.getId();
                    }
                    if (task.getStartTime() != null && manager.hasTimeOverlaps(task)) {
                        throw new IllegalStateException();
                    }
                    switch (task.getType()) {
                        case TASK -> {
                            manager.tasks.put(task.getId(), task);
                            manager.addToPrioritizedTasks(task);
                        }
                        case EPIC -> {
                            Epic epic = (Epic) task;
                            manager.epics.put(task.getId(), epic);
                        }
                        case SUBTASK -> {
                            Subtask subtask = (Subtask) task;
                            manager.subtasks.put(task.getId(), subtask);
                            manager.addToPrioritizedTasks(subtask);
                        }
                    }

                }
            }
            manager.nextId = maxId + 1;
            for (Subtask subtask : manager.subtasks.values()) {
                Epic epic = manager.epics.get(subtask.getEpicId());
                if (epic != null) {
                    epic.addSubtaskId(subtask.getId());
                }
            }
            for (Epic epic : manager.epics.values()) {
                manager.updateEpicTime(epic.getId());
            }


        } catch (IOException e) {
            throw new ManagerLoadException("ошибка загрузки", e);
        }
        return manager;
    }

    private static Task fromString(String value) {
        String[] parts = value.split(",");
        try {
            int id = Integer.parseInt(parts[0]);
            TaskType type = TaskType.valueOf(parts[1].trim());
            String name = parts[2];
            TaskStatus status = TaskStatus.valueOf(parts[3]);
            String description = parts[4];

            LocalDateTime startTime = !parts[5].equals("null") ? LocalDateTime.parse(parts[5]) : null;
            Duration duration = Duration.ofMinutes(Long.parseLong(parts[6]));

            Task task = switch (type) {
                case TASK:
                    Task t = new Task(name, description, status, startTime, duration);
                    t.setId(id);
                    yield t;

                case EPIC:
                    Epic e = new Epic(name, description);
                    e.setId(id);
                    e.setStatus(status);
                    if (startTime != null) {
                        e.setStartTime(startTime);
                        e.setDuration(duration);
                    }
                    yield e;

                case SUBTASK:
                    Subtask sub = new Subtask(name, description, status, Integer.parseInt(parts[7]),
                            startTime, duration);
                    sub.setId(id);
                    yield sub;
            };
            return task;

        } catch (Exception e) {
            return null;
        }

    }

}
