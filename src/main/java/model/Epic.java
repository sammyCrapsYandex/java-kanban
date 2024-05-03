package model;

import java.util.HashSet;
import java.util.List;

public class Epic extends Task {
    private final HashSet<Integer> subtasks;

    public Epic(String name, String description, int id) {
        super(name, description, id);
        subtasks = new HashSet<>();
    }

    public HashSet<Integer> getSubtasks() {
        return subtasks;
    }

    public void addSubtaskById(Integer subtaskId) {
        subtasks.add(subtaskId);
    }

    public void deleteSubtaskById(Integer subtaskId) {
        subtasks.remove(subtaskId);
    }

    public void calculateStatus(List<Subtask> subtasksByEpic) {
        boolean isAllNew = true;
        boolean isAllDone = true;

        for (Subtask subtask : subtasksByEpic) {
            switch (subtask.getStatus()) {
                case NEW -> isAllDone = false;
                case DONE -> isAllNew = false;
                default -> {
                    isAllNew = false;
                    isAllDone = false;
                }
            }
        }
        if (isAllNew) {
            setStatus(TaskStatus.NEW);
        } else if (isAllDone) {
            setStatus(TaskStatus.DONE);
        } else {
            setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}
