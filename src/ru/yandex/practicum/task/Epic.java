package ru.yandex.practicum.task;

import ru.yandex.practicum.taskmanager.TaskManager;

import java.util.ArrayList;
import java.util.List;
import java.time.Duration;
import java.time.LocalDateTime;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW, null, Duration.ZERO);
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskId(int subtaskId) {
        if (subtaskId != this.id) {
            subtaskIds.add(subtaskId);
        }
    }

    public void removeSubtaskId(int subtaskId) {
        subtaskIds.remove((Integer) subtaskId);
    }

    public void updateStatus(List<Subtask> subtasks) {
        if (subtasks.isEmpty()) {
            this.status = TaskStatus.NEW;
            return;
        }

        boolean allNew = true;
        boolean allDone = true;
        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() != TaskStatus.NEW) {
                allNew = false;
            }
            if (subtask.getStatus() != TaskStatus.DONE) {
                allDone = false;
            }
        }
        if (allNew) {
            this.status = TaskStatus.NEW;
        } else if (allDone) {
            this.status = TaskStatus.DONE;
        } else {
            this.status = TaskStatus.IN_PROGRESS;
        }
    }

    public void updateTime(TaskManager manager) {
        if (manager == null || subtaskIds.isEmpty()) {
            this.startTime = null;
            this.duration = Duration.ZERO;
            this.endTime = null;
            return;
        }

        LocalDateTime minStartTime = null;
        LocalDateTime maxFinishTime = null;
        Duration totalDuration = Duration.ZERO;
        boolean hasTimeData = false;

        for (int subtaskId : subtaskIds) {
            Subtask subtask = manager.getSubtaskById(subtaskId);
            if (subtask == null || subtask.getStartTime() == null) continue;
            hasTimeData = true;
            LocalDateTime start = subtask.getStartTime();
            LocalDateTime finish = subtask.getEndTime();

            if (minStartTime == null || start.isBefore(minStartTime)) {
                minStartTime = start;
            }
            if (maxFinishTime == null || finish.isAfter(maxFinishTime)) {
                maxFinishTime = finish;
            }
            totalDuration = totalDuration.plus(subtask.getDuration());


        }
        if (hasTimeData) {
            this.startTime = minStartTime;
            this.duration = totalDuration;
            this.endTime = maxFinishTime;
        } else {
            this.startTime = null;
            this.duration = Duration.ZERO;
            this.endTime = null;
        }
    }

    public void setFinishTime(LocalDateTime finishTime) {
        this.endTime = finishTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + "'" +
                ", description='" + description + "'" +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration.toMinutes() + "m" +
                ", finishTime=" + endTime +
                ", subtaskIds=" + subtaskIds +
                '}';
    }
}
