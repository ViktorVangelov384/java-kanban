package ru.yandex.practicum.task;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, TaskStatus status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
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
        return "Subtask{" +
                "id=" + id + ", " +
                "name= '" + name + "', " +
                "description= '" + description + "', " +
                "status=" + status + ", " +
                "epicId=" + epicId +
                "}";
    }
}
