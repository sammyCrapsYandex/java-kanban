package builder;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.InMemoryTaskManager;
import service.TaskManager;
import utils.Managers;

import java.util.LinkedList;
import java.util.List;

/**
 * Utility class for preparing test data
 */
public class TestDataBuilder {

    private final TaskManager taskManager;

    public TestDataBuilder(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public TestDataBuilder() {
        this.taskManager = Managers.getDefault();
    }

    public TaskManager buildTaskManager() {
        return new InMemoryTaskManager();
    }

    public Task buildTask(String title, String description) {
        return new Task(title, description, taskManager.getCounter());
    }

    public static Task buildTask(int id, String title, String description, TaskStatus status) {
        Task task = new Task(title, description, id);
        task.setStatus(status);
        return task;
    }

    public Epic buildEpic(String title, String description) {
        return new Epic(title, description, taskManager.getCounter());
    }

    public static Epic buildEpic(int id, String title, String description) {
        return new Epic(title, description, id);
    }

    public Subtask buildSubtask(String title, String description, int epicId) {
        return new Subtask(title, description, taskManager.getCounter(), epicId);
    }

    public static Subtask buildSubtask(int id, String title, String description, int epicId) {
        return new Subtask(title, description, id, epicId);
    }

    public Task buildCopyTask(Task task) {
        return buildTask(task.getId(), task.getName(), task.getDescription(), task.getStatus());
    }

    public Epic buildCopyEpic(Epic epic) {
        Epic result = buildEpic(epic.getId(), epic.getName(), epic.getDescription());
        for (int subTaskId : epic.getSubtasks()) {
            result.addSubtaskById(subTaskId);
        }
        return result;
    }

    public Subtask buildCopySubtask(Subtask subtask) {
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