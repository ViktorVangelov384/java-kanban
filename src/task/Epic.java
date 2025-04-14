package task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task{
    ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }
    public void addSubtaskId(int subtaskId) {
        subtaskIds.add(subtaskId);
    }
    public void removeSubtaskId(int subtaskId) {
        subtaskIds.remove(subtaskId);
    }
    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + "'" +
                ", description='" + getDescription() + "'" +
                ", status=" + getStatus() +
                ", subtaskIds=" + subtaskIds +
                '}';
    }
}
