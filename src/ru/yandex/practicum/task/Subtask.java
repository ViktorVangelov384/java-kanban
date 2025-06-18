package ru.yandex.practicum.task;

import java.time.LocalDateTime;
import java.time.Duration;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, TaskStatus status, int epicId, LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, TaskStatus status, int epicId) {
        this(name, description, status, epicId, null, Duration.ZERO);
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return String.format(
                "Subtask{id=%d, name='%s', description='%s', status=%s, " +
                        "startTime=%s, duration=%d min, endTime=%s, epicId=%d}",
                id, name, description, status,
                startTime != null ? startTime : "null",
                duration.toMinutes(),
                endTime != null ? endTime : "null",
                epicId
        );
    }
}
