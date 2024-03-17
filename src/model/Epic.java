package model;

import java.util.HashSet;

public class Epic extends Task {
    private final HashSet<Integer> subtasks;

    public Epic(String name, String description, int id) {
        super(name, description, id);
        subtasks = new HashSet<>();
    }

    public HashSet<Integer> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Integer subtaskId) {
        subtasks.add(subtaskId);
    }

    public void deleteSubtask(Integer subtaskId) {
        subtasks.remove(subtaskId);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subtasks=" + subtasks +
                '}';
    }
}
