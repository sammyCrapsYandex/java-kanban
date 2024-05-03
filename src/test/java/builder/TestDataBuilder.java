package builder;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.util.LinkedList;
import java.util.List;

/**
 * Utility class for preparing test data
 */
public class TestDataBuilder {
    private TestDataBuilder() {
    }

    public static TaskManager buildTaskManager() {
        return new InMemoryTaskManager();
    }

    public static Task buildTask(String title, String description) {
        return new Task(title, description, InMemoryTaskManager.getId());
    }

    public static Task buildTask(int id, String title, String description, TaskStatus status) {
        Task task = new Task(title, description, id);
        task.setStatus(status);
        return task;
    }

    public static Epic buildEpic(String title, String description) {
        return new Epic(title, description, InMemoryTaskManager.getId());
    }

    public static Epic buildEpic(int id, String title, String description) {
        return new Epic(title, description, id);
    }

    public static Subtask buildSubtask(String title, String description, int epicId) {
        return new Subtask(title, description, InMemoryTaskManager.getId(), epicId);
    }

    public static Subtask buildSubtask(int id, String title, String description, int epicId) {
        return new Subtask(title, description, id, epicId);
    }

    public static Task buildCopyTask(Task task) {
        return buildTask(task.getId(), task.getName(), task.getDescription(), task.getStatus());
    }

    public static Epic buildCopyEpic(Epic epic) {
        Epic result = buildEpic(epic.getId(), epic.getName(), epic.getDescription());
        for (int subTaskId : epic.getSubtasks()) {
            result.addSubtaskById(subTaskId);
        }
        return result;
    }

    public static Subtask buildCopySubtask(Subtask subtask) {
        Subtask result = buildSubtask(subtask.getId(), subtask.getName(), subtask.getDescription(), subtask.getEpicId());
        result.setStatus(subtask.getStatus());
        return result;
    }

    public static List<Task> buildTasks() {
        return new LinkedList<>(
                List.of(buildEpic(1, "epic1", "description"),
                        buildTask(2, "task1", "d", TaskStatus.NEW),
                        buildSubtask(3, "subtask1", "notes", 1),
                        buildEpic(4, "epic1", "description"),
                        buildSubtask(5, "subtask2", "notes", 1)));
    }

}