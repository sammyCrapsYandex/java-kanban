package service;

import exception.AlreadyExistsException;
import exception.EpicDoesntExistException;
import model.Epic;
import model.Subtask;
import model.Task;
import utils.Managers;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private int counter = 1;
    final HashMap<Integer, Task> tasks = new HashMap<>();
    final HashMap<Integer, Epic> epics = new HashMap<>();
    final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    @Override
    public int getCounter() {
        return counter++;
    }

    @Override
    public Task addTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            throw new AlreadyExistsException("object already exists");
        }
        return task;
    }

    @Override
    public Subtask addSubTask(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())) {
            throw new EpicDoesntExistException("Epic does not exist");
        }
        if (!subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            epics.get(subtask.getEpicId()).addSubtaskById(subtask.getId());
            updateEpicStatus(epics.get(subtask.getEpicId()));
        } else {
            throw new AlreadyExistsException("Object already exists");
        }
        return subtask;
    }

    @Override
    public Epic addEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        } else {
            throw new AlreadyExistsException("object already exists");
        }
        return epic;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        for (HashMap.Entry<Integer, Task> entry : tasks.entrySet()) {
            allTasks.add(entry.getValue());
        }
        return allTasks;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> allEpics = new ArrayList<>();
        for (HashMap.Entry<Integer, Epic> entry : epics.entrySet()) {
            allEpics.add(entry.getValue());
        }
        return allEpics;
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> allSubTask = new ArrayList<>();
        for (HashMap.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
            allSubTask.add(entry.getValue());
        }
        return allSubTask;
    }

    @Override
    public Set<Integer> getSubtasksByEpicId(int id) {
        if (!epics.containsKey(id)) {
            return null;
        }
        return new HashSet<>(epics.get(id).getSubtasks());
    }

    @Override
    public void clearTasks() {
        tasks.values().forEach(t -> historyManager.remove(t.getId()));
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        for (HashMap.Entry<Integer, Epic> entry : epics.entrySet()) {
            HashSet<Integer> subtasksByEpic = epics.get(entry.getKey()).getSubtasks();
            if (!subtasksByEpic.isEmpty()) {
                for (int subTaskId : subtasksByEpic) {
                    subtasks.remove(subTaskId);
                    historyManager.remove(subTaskId);
                }
            }
        }
        epics.values().forEach(e -> historyManager.remove(e.getId()));
        epics.clear();
    }

    @Override
    public void clearSubtasks() {
        for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
            int epicId = subtasks.get(entry.getKey()).getEpicId();
            epics.get(epicId).deleteSubtaskById(entry.getKey());
            updateEpicStatus(epics.get(epicId));
        }
        subtasks.values().forEach(st -> historyManager.remove(st.getId()));
        subtasks.clear();
    }

    @Override
    public Task getTaskById(int taskId) {
        historyManager.add(tasks.get(taskId));
        return tasks.getOrDefault(taskId, null);
    }

    @Override
    public Subtask getSubTaskById(int taskId) {
        historyManager.add(subtasks.get(taskId));
        return subtasks.getOrDefault(taskId, null);
    }

    @Override
    public Epic getEpicById(int taskId) {
        historyManager.add(epics.get(taskId));
        return epics.getOrDefault(taskId, null);
    }

    @Override
    public void deleteTaskById(int taskId) {
        if (tasks.containsKey(taskId)) {
            tasks.remove(taskId);
            historyManager.remove(taskId);
        }
    }

    @Override
    public void deleteSubTaskById(int subTaskId) {
        if (subtasks.containsKey(subTaskId)) {
            int epicId = subtasks.get(subTaskId).getEpicId();
            subtasks.remove(subTaskId);

            epics.get(epicId).deleteSubtaskById(subTaskId);
            updateEpicStatus(epics.get(epicId));
            historyManager.remove(subTaskId);
        }
    }

    @Override
    public void deleteEpicById(int epicId) {
        if (epics.containsKey(epicId)) {
            HashSet<Integer> subtasksByEpic = epics.get(epicId).getSubtasks();
            if (!subtasksByEpic.isEmpty()) {
                for (int subTaskId : subtasksByEpic) {
                    subtasks.remove(subTaskId);
                    historyManager.remove(subTaskId);
                }
            }
            epics.remove(epicId);
            historyManager.remove(epicId);
        }
    }

    @Override
    public void updateTask(Task newTask) {
        int newTaskId = newTask.getId();
        if (!tasks.containsKey(newTaskId)) return;
            tasks.put(newTaskId, newTask);
    }

    @Override
    public void updateSubTask(Subtask newSubTask) {
        int newSubTaskId = newSubTask.getId();
        if (!subtasks.containsKey(newSubTaskId)) return;

        subtasks.put(newSubTaskId, newSubTask);
        int epicId = subtasks.get(newSubTaskId).getEpicId();
        updateEpicStatus(epics.get(epicId));
    }

    @Override
    public void updateEpic(Epic newEpic) {
        int newEpicId = newEpic.getId();
        if (!epics.containsKey(newEpicId)) return;
        if (!epics.get(newEpicId).equals(newEpic)) {
            epics.put(newEpicId, newEpic);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void updateEpicStatus(Epic epic) {
        List<Subtask> subtasksByEpic = epic
                .getSubtasks()
                .stream()
                .map(this::getSubTaskById).toList();
        epic.calculateStatus(subtasksByEpic);
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }
}
