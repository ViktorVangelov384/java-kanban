package ru.yandex.practicum.task;

import java.util.Objects;
import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    protected int id;
    protected String name;
    protected String description;
    protected TaskStatus status;
    protected Duration duration;
    protected LocalDateTime startTime;


    public Task(String name, String description, TaskStatus status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;

    }

    public Task(String name, String description, TaskStatus status) {
        this(name, description, status, null, Duration.ZERO);
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;

    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;

    }

    public LocalDateTime getEndTime() {
        return startTime != null ? startTime.plus(duration) : null;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration.toMinutes() + "m" +
                ", endTime=" + getEndTime() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
