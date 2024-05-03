package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public interface TaskManager {

    Task addTask(Task newTask);

    Subtask addSubTask(Subtask newSubtask);

    Epic addEpic(Epic newEpic);

    HashMap<Integer, Task> getAllTasks();

    HashMap<Integer, Subtask> getAllSubtasks();

    HashMap<Integer, Epic> getAllEpics();

    Set<Integer> getSubtasksByEpicId(int id);

    void clearTasks();

    void clearSubtasks();

    void clearEpics();

    Task getTaskById(int taskId);

    Subtask getSubTaskById(int taskId);

    Epic getEpicById(int taskId);

    void deleteTaskById(int taskId);

    void deleteSubTaskById(int subTaskId);

    void deleteEpicById(int epicId);

    void updateTask(Task newTask);

    void updateSubTask(Subtask newSubTask);

    void updateEpic(Epic newEpic);

    ArrayList<Task> getHistory();


}
