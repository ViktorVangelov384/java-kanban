package task;

public class Subtask extends Task{
    private final int epicId;
    public Subtask(String name, String description, TaskStatus status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
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
