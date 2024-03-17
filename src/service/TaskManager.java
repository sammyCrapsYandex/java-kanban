package service;

import exception.AlreadyExistsException;
import exception.EpicDoesntExistException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.HashMap;
import java.util.HashSet;

public class TaskManager {

    private static int id = 1;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
    }

    public static int getId() {
        return id++;
    }

    public void createTask(Task newTask){
        if (!tasks.containsKey(newTask.getId())) {
            tasks.put(newTask.getId(), newTask);
        } else {
            throw new AlreadyExistsException("object already exists");
        }
    }

    public void createSubTask(Subtask newSubtask){
        if (!epics.containsKey(newSubtask.getEpicId())) {
            throw new EpicDoesntExistException("Epic does not exist");
        }
        if (!subtasks.containsKey(newSubtask.getId())) {
            subtasks.put(newSubtask.getId(), newSubtask);
            epics.get(newSubtask.getEpicId()).addSubtask(newSubtask.getId());
            updateEpicStatus(epics.get(newSubtask.getEpicId()));
        } else {
            throw new AlreadyExistsException("Object already exists");
        }
    }

    public void createEpic(Epic newEpic){
        if (!epics.containsKey(newEpic.getId())) {
            epics.put(newEpic.getId(), newEpic);
        } else {
            throw new AlreadyExistsException("object already exists");
        }
    }

    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getAllEpics() {
        return epics;
    }

    public HashMap<Integer, Subtask> getAllSubtasks() {
        return subtasks;
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        for (HashMap.Entry<Integer, Epic> entry: epics.entrySet()) {
            HashSet<Integer> subtasksByEpic = epics.get(entry.getKey()).getSubtasks();
            if (!subtasksByEpic.isEmpty()) {
                for (int subTaskId : subtasksByEpic) {
                    subtasks.remove(subTaskId);
                }
            }
        }
        epics.clear();
    }

    public void deleteAllSubtasks() {
        for (HashMap.Entry<Integer, Subtask> entry: subtasks.entrySet()) {
            int epicId = subtasks.get(entry.getKey()).getEpicId();
            epics.get(epicId).deleteSubtask(entry.getKey());
            updateEpicStatus(epics.get(epicId));
        }
        subtasks.clear();
    }

    public Task getTaskById(int taskId) {
        return tasks.getOrDefault(taskId, null);
    }

    public Task getSubTaskById(int taskId) {
        return subtasks.getOrDefault(taskId, null);
    }

    public Task getEpicById(int taskId) {
        return epics.getOrDefault(taskId, null);
    }

    public void deleteTaskById(int taskId) {
        if (tasks.containsKey(taskId)) {
            tasks.remove(taskId);
        }
    }

    public void deleteSubTaskById(int subTaskId) {
        if (subtasks.containsKey(subTaskId)) {
            int epicId = subtasks.get(subTaskId).getEpicId();
            epics.get(epicId).deleteSubtask(subTaskId);
            subtasks.remove(subTaskId);
            updateEpicStatus(epics.get(epicId));
        }
    }

    public void deleteEpicById(int epicId) {
        if (epics.containsKey(epicId)) {
            HashSet<Integer> subtasksByEpic = epics.get(epicId).getSubtasks();
            if (!subtasksByEpic.isEmpty()) {
                for (int subTaskId : subtasksByEpic) {
                    subtasks.remove(subTaskId);
                }
            }
            epics.remove(epicId);
        }
    }

    public void updateTask(Task newTask) {
        int newTaskId = newTask.getId();
        if (!tasks.containsKey(newTaskId)) return;
        if (!tasks.get(newTaskId).equals(newTask)) {
            tasks.put(newTaskId, newTask);
        }
    }

    public void updateSubTask(Subtask newSubTask) {
        int newSubTaskId = newSubTask.getId();
        if (!subtasks.containsKey(newSubTaskId)) return;

        subtasks.put(newSubTaskId, newSubTask);
        int epicId = subtasks.get(newSubTaskId).getEpicId();
        updateEpicStatus(epics.get(epicId));
    }

    public void updateEpicStatus(Epic epic) {
        boolean isAllNew = true;
        boolean isAllDone = true;
        HashSet<Integer> subtasksByEpic = epic.getSubtasks();
        for (Integer subtaskId : subtasksByEpic) {
            Subtask subtask = subtasks.get(subtaskId);
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
            epic.setStatus(TaskStatus.NEW);
        } else if (isAllDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    public void updateEpic(Epic newEpic) {
        int newEpicId = newEpic.getId();
        if (!epics.containsKey(newEpicId)) return;
        if (!epics.get(newEpicId).equals(newEpic)) {
            epics.put(newEpicId, newEpic);
        }
    }

    @Override
    public String toString() {
        return "TaskManager{" +
                "tasks=" + tasks +
                ", epics=" + epics +
                ", subtasks=" + subtasks +
                '}';
    }
}
