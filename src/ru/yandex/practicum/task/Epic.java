package ru.yandex.practicum.task;

import java.util.ArrayList;
import java.util.List;
import java.time.Duration;
import java.time.LocalDateTime;

public class Epic extends Task {
    private final ArrayList<Integer> subtaskIds = new ArrayList<>();
    protected LocalDateTime endTime;

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

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
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
                ", endTime=" + endTime +
                ", subtaskIds=" + subtaskIds +
                '}';
    }
}
