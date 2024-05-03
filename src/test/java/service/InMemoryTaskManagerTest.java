package service;

import builder.TestDataBuilder;
import exception.AlreadyExistsException;
import exception.EpicDoesntExistException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void getAllTaskShouldReturnList() {
        final Task task1 = TestDataBuilder.buildTask("t1", "d1");
        final Task task2 = TestDataBuilder.buildTask("t2", "d2");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        final HashMap<Integer, Task> expected = new HashMap<>();
        expected.put(task1.getId(), task1);
        expected.put(task2.getId(), task2);

        final HashMap<Integer, Task> actual = taskManager.getAllTasks();

        Assertions.assertAll(
                () -> assertNotNull(actual, "HashMap was not returned."),
                () -> Assertions.assertEquals(expected, actual, "HashMap returned is not correct.")
        );
    }

    @Test
    void getAllTaskShouldReturnEmptyListWhenThereIsNoTasks() {
        final HashMap<Integer, Task> expected = new HashMap<>();

        final HashMap<Integer, Task> actual = taskManager.getAllTasks();

        Assertions.assertAll(
                () -> assertNotNull(actual, "HashMap should not be null."),
                () -> Assertions.assertEquals(expected, actual, "HashMap returned is not correct.")
        );
    }

    @Test
    void getAllEpicsShouldReturnList() {
        final Epic epic1 = TestDataBuilder.buildEpic("e1", "d1");
        final Epic epic2 = TestDataBuilder.buildEpic("e2", "d2");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        final HashMap<Integer, Epic> expected = new HashMap<>();
        expected.put(epic1.getId(), epic1);
        expected.put(epic2.getId(), epic2);

        final HashMap<Integer, Epic> actual = taskManager.getAllEpics();

        Assertions.assertAll(
                () -> assertNotNull(actual, "HashMap was not returned."),
                () -> Assertions.assertEquals(expected, actual, "HashMap returned is not correct.")
        );
    }

    @Test
    void getAllEpicShouldReturnEmptyListWhenThereIsNoEpic() {
        final HashMap<Integer, Epic> expected = new HashMap<>();

        final HashMap<Integer, Epic> actual = taskManager.getAllEpics();

        Assertions.assertAll(
                () -> assertNotNull(actual, "HashMap should not be null."),
                () -> Assertions.assertEquals(expected, actual, "HashMap returned is not correct.")
        );
    }

    @Test
    void getAllSubtasksShouldReturnList() {
        final Epic epic1 = TestDataBuilder.buildEpic("e1", "d1");
        taskManager.addEpic(epic1);
        final Subtask subtask1 = TestDataBuilder.buildSubtask("st1", "d1", epic1.getId());
        final Subtask subtask2 = TestDataBuilder.buildSubtask("st2", "d2", epic1.getId());
        taskManager.addSubTask(subtask1);
        taskManager.addSubTask(subtask2);
        final HashMap<Integer, Subtask> expected = new HashMap<>();
        expected.put(subtask1.getId(), subtask1);
        expected.put(subtask2.getId(), subtask2);

        final HashMap<Integer, Subtask> actual = taskManager.getAllSubtasks();

        Assertions.assertAll(
                () -> assertNotNull(actual, "HashMap was not returned."),
                () -> Assertions.assertEquals(expected, actual, "HashMap returned is not correct.")
        );
    }

    @Test
    void getAllSubtasksShouldReturnEmptyListWhenThereIsNoSubtasks() {
        final HashMap<Integer, Subtask> expected = new HashMap<>();

        final HashMap<Integer, Subtask> actual = taskManager.getAllSubtasks();

        Assertions.assertAll(
                () -> assertNotNull(actual, "HashMap should not be null."),
                () -> Assertions.assertEquals(expected, actual, "HashMap returned is not correct.")
        );
    }

    @Test
    void clearTaskShouldDeleteAllTasksFromTheMemory() {
        final Task task1 = TestDataBuilder.buildTask("t1", "d1");
        final Task task2 = TestDataBuilder.buildTask("t2", "d2");
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        taskManager.clearTasks();

        Assertions.assertTrue(taskManager.getAllTasks().isEmpty(), "Tasks were not deleted.");
    }

    @Test
    void clearTaskShouldDeleteAllTasksFromTheHistory() {
        getHistoryReady();
        final List<Integer> taskIdsInMemory = taskManager.getAllTasks().keySet().stream().toList();

        final List<Task> historyBeforeClear = taskManager.getHistory();

        taskManager.clearTasks();
        final List<Task> historyAfterClear = taskManager.getHistory();
        final List<Task> actualTasksInHistory = historyAfterClear.stream()
                .filter(t -> taskIdsInMemory.contains(t.getId())).toList();

        Assertions.assertAll(
                () -> Assertions.assertTrue(historyAfterClear.size() < historyBeforeClear.size(),
                        "History should be reduced"),
                () -> Assertions.assertTrue(actualTasksInHistory.isEmpty(),
                        "Deleted tasks should not remain in the history")
        );
    }

    @Test
    void clearEpicsShouldDeleteAllEpicsAndSubtasksFromTheMemory() {
        Epic epic1 = TestDataBuilder.buildEpic("e1", "d1");
        Epic epic2 = TestDataBuilder.buildEpic("e2", "d2");
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        taskManager.clearEpics();

        Assertions.assertAll(
                () -> Assertions.assertTrue(taskManager.getAllEpics().isEmpty(),
                        "Epics was not deleted."),
                () -> Assertions.assertTrue(taskManager.getAllSubtasks().isEmpty(),
                        "Subtasks were not deleted.")
        );
    }

    @Test
    void clearEpicShouldDeleteAllEpicsAndSubtasksFromTheHistory() {
        getHistoryReady();
        final List<Integer> epicsIdsInMemory = taskManager.getAllEpics().keySet().stream().toList();
        final List<Integer> subtasksIdsInMemory = taskManager.getAllSubtasks().keySet().stream().toList();
        final List<Task> historyBeforeClear = taskManager.getHistory();

        taskManager.clearEpics();
        final List<Task> historyAfterClear = taskManager.getHistory();
        final List<Task> actualEpicsInHistory = historyAfterClear.stream()
                .filter(t -> epicsIdsInMemory.contains(t.getId())).toList();
        final List<Task> actualSubtasksInHistory = historyAfterClear.stream()
                .filter(t -> subtasksIdsInMemory.contains(t.getId())).toList();

        Assertions.assertAll(
                () -> Assertions.assertTrue(historyAfterClear.size() < historyBeforeClear.size(),
                        "History should be reduced"),
                () -> Assertions.assertTrue(actualEpicsInHistory.isEmpty(),
                        "Deleted epics should not remain in the history"),
                () -> Assertions.assertTrue(actualSubtasksInHistory.isEmpty(),
                        "Deleted subtasks should not remain in the history")
        );
    }

    @Test
    void clearSubtasksShouldDeleteAllSubtasksFromTheMemoryAndFromEpics() {
        Epic epic1 = TestDataBuilder.buildEpic("e1", "d1");
        taskManager.addEpic(epic1);
        Subtask subtask1 = TestDataBuilder.buildSubtask("st1", "d1", epic1.getId());
        Subtask subtask2 = TestDataBuilder.buildSubtask("st2", "d2", epic1.getId());
        taskManager.addSubTask(subtask1);
        taskManager.addSubTask(subtask2);

        taskManager.clearSubtasks();
        ArrayList<Integer> subtasksFromEpics = new ArrayList<>();
        for (HashMap.Entry<Integer, Epic> entry : taskManager.getAllEpics().entrySet()) {
            subtasksFromEpics.addAll(taskManager.getSubtasksByEpicId(entry.getKey()));
        }

        Assertions.assertAll(
                () -> Assertions.assertTrue(taskManager.getAllSubtasks().isEmpty(),
                        "Subtasks was not deleted."),
                () -> Assertions.assertEquals(List.of(), subtasksFromEpics,
                        "Subtasks were not deleted from Epics.")
        );
    }

    @Test
    void clearSubtasksShouldDeleteAllSubtasksFromTheHistory() {
        getHistoryReady();
        final List<Integer> subtasksIdsInMemory = taskManager.getAllSubtasks().keySet().stream().toList(); // size = 2, [3,5]
        final List<Task> historyBeforeClear = taskManager.getHistory();

        taskManager.clearEpics();
        final List<Task> historyAfterClear = taskManager.getHistory();
        final List<Task> actualSubtasksInHistory = historyAfterClear.stream()
                .filter(t -> subtasksIdsInMemory.contains(t.getId())).toList();

        Assertions.assertAll(
                () -> Assertions.assertTrue(historyAfterClear.size() < historyBeforeClear.size(),
                        "History should be reduced"),
                () -> Assertions.assertEquals(0, actualSubtasksInHistory.size(),
                        "Deleted subtasks should not remain in the history")
        );
    }

    @Test
    void deleteTaskShouldDeleteTaskBiIdFromTheMemoryAndHistory() {
        final Task task1 = TestDataBuilder.buildTask("t1", "d1");
        final Task task2 = TestDataBuilder.buildTask("t2", "d2");
        taskManager.addTask(task1);
        final Task taskToDelete = taskManager.addTask(task2);
        final int idToDelete = taskToDelete.getId();
        taskManager.getTaskById(idToDelete);
        final int historySizeBefore = taskManager.getHistory().size();

        taskManager.deleteTaskById(taskToDelete.getId());
        final boolean isDeletedFromHistory = !taskManager.getHistory().contains(taskToDelete);
        final int actualHistorySize = taskManager.getHistory().size();

        Assertions.assertAll(
                () -> Assertions.assertNull(taskManager.getTaskById(task2.getId()),
                        "Task was not deleted."),
                () -> Assertions.assertTrue(isDeletedFromHistory, "Task was not deleted from the history."),
                () -> Assertions.assertTrue(actualHistorySize < historySizeBefore,
                        "THistory should reduce its size.")
        );
    }

    @Test
    void deleteEpicShouldDeleteEpicsByIdAndItsSubtasksFromTheMemoryAndHistory() {
        taskManager.addEpic(TestDataBuilder.buildEpic("e1", "d1"));
        final Epic epicToDelete = taskManager.addEpic(TestDataBuilder.buildEpic("e2", "d2"));
        final Subtask sbToDelete = taskManager.addSubTask(
                TestDataBuilder.buildSubtask("sb1", "d", epicToDelete.getId()));
        taskManager.getEpicById(epicToDelete.getId());
        taskManager.getSubTaskById(sbToDelete.getId());
        final List<Task> historyBeforeDeleting = taskManager.getHistory();

        taskManager.deleteEpicById(epicToDelete.getId());
        final List<Task> actualHistory = taskManager.getHistory();
        final Epic actualEpicInMemory = taskManager.getEpicById(epicToDelete.getId());
        final List<Subtask> actualSubtasksInMemory = taskManager.getAllSubtasks().values().stream()
                .filter(subtask -> subtask.getEpicId() == epicToDelete.getId())
                .collect(Collectors.toList());
        final boolean isEpicDeletedFromHistory = !actualHistory.contains(epicToDelete);
        final boolean isSubtaskDeletedFromHistory = !actualHistory.contains(sbToDelete);

        Assertions.assertAll(
                () -> Assertions.assertNull(actualEpicInMemory, "Epic was not deleted."),
                () -> Assertions.assertEquals(List.of(), actualSubtasksInMemory,
                        "Subtasks were not deleted."),
                () -> Assertions.assertTrue(actualHistory.size() < historyBeforeDeleting.size(),
                        "History had not reduced it's size."),
                () -> Assertions.assertTrue(isEpicDeletedFromHistory,
                        "Epic was not deleted from the history."),
                () -> Assertions.assertTrue(isSubtaskDeletedFromHistory,
                        "Corresponding subtask was not deleted from the history.")
        );
    }

    @Test
    void deleteSubtaskShouldDeleteSubtaskFromTheMemoryFromItsEpicAndFromTheHistory() {
        final Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("e1", "d1"));
        taskManager.addSubTask(TestDataBuilder.buildSubtask("st1", "d1", epicInMemory.getId()));
        final Subtask subtaskToDelete = taskManager.addSubTask(
                TestDataBuilder.buildSubtask("st2", "d2", epicInMemory.getId()));
        final int idSubtaskToDelete = subtaskToDelete.getId();
        taskManager.getSubTaskById(idSubtaskToDelete);
        final List<Task> historyBeforeDeleting = taskManager.getHistory();

        taskManager.deleteSubTaskById(idSubtaskToDelete);
        final List<Task> actualHistory = taskManager.getHistory();
        final Set<Integer> subtasksFromEpic = taskManager.getSubtasksByEpicId(epicInMemory.getId());
        boolean isDeletedFromEpic = subtasksFromEpic.stream()
                .noneMatch((st) -> st == idSubtaskToDelete);
        boolean isDeletedFromTheHistory = !actualHistory.contains(idSubtaskToDelete);

        Assertions.assertAll(
                () -> Assertions.assertNull(taskManager.getSubTaskById(idSubtaskToDelete),
                        "Subtask was not deleted."),
                () -> Assertions.assertTrue(isDeletedFromEpic, "Subtask was not deleted from Epic."),
                () -> Assertions.assertTrue(actualHistory.size() < historyBeforeDeleting.size(),
                        "History had not reduced it's size."),
                () -> Assertions.assertTrue(isDeletedFromTheHistory,
                        "Subtask was not deleted from the history.")
        );
    }

    @Test
    void getTaskByIdShouldReturnTaskWhenIdIsValid() {
        Task taskInMemory = taskManager.addTask(TestDataBuilder.buildTask("t", "d"));
        int taskInMemoryId = taskInMemory.getId();

        Task returnedTask = taskManager.getTaskById(taskInMemoryId);

        Assertions.assertAll(
                () -> assertNotNull(returnedTask, "Task was not returned."),
                () -> Assertions.assertEquals(taskInMemory, returnedTask, "Returned wrong task. ")
        );
    }

    @Test
    void getTaskByIdShouldReturnNullWhenIdIsNotValid() {
        Task returnedTask = taskManager.getTaskById(0);

        Assertions.assertNull(returnedTask, "Task was not returned.");
    }

    @Test
    void getTaskByIdShouldSaveTaskToHistory() {
        Task taskInMemory = taskManager.addTask(TestDataBuilder.buildTask("t", "d"));
        int taskInMemoryId = taskInMemory.getId();
        List<Task> expected = List.of(taskInMemory);

        taskManager.getTaskById(taskInMemoryId);
        List<Task> actual = taskManager.getHistory();

        Assertions.assertIterableEquals(expected, actual, "Task was not added to the history.");
    }

    @Test
    void getTaskByInvalidIdShouldNotSaveTaskToHistory() {
        int taskId = 0;
        List<Task> expected = List.of();

        taskManager.getTaskById(taskId);
        List<Task> actual = taskManager.getHistory();

        Assertions.assertIterableEquals(expected, actual, " Null was added to the history.");
    }

    // getEpicById();
    @Test
    void getEpicByIdShouldReturnEpicWhenIdIsValid() {
        Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("e", "d"));
        int epicInMemoryId = epicInMemory.getId();

        Epic returnedEpic = taskManager.getEpicById(epicInMemoryId);

        Assertions.assertAll(
                () -> assertNotNull(returnedEpic, "Epic was not returned."),
                () -> Assertions.assertEquals(epicInMemory, returnedEpic, "Returned wrong epic. ")
        );
    }

    @Test
    void getEpicByIdShouldReturnNullWhenIdIsNotValid() {
        Epic returnedEpic = taskManager.getEpicById(0);

        Assertions.assertNull(returnedEpic, "Epic was not returned.");
    }

    @Test
    void getEpicByIdShouldSaveEpicToHistory() {
        Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("t", "d"));
        int epicInMemoryId = epicInMemory.getId();
        List<Epic> expected = List.of(epicInMemory);

        taskManager.getEpicById(epicInMemoryId);
        List<Task> actual = taskManager.getHistory();

        Assertions.assertIterableEquals(expected, actual, "Task was not added to the history.");
    }

    @Test
    void getEpicByInvalidIdShouldNotSaveEpicToHistory() {
        int epicId = 0;
        List<Task> expected = List.of();

        taskManager.getEpicById(epicId);
        List<Task> actual = taskManager.getHistory();

        Assertions.assertIterableEquals(expected, actual, " Null was added to the history.");
    }

    // getSubTaskById();
    @Test
    void getSubTaskByIdShouldReturnSubtaskWhenIdIsValid() {
        Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("e", "d"));
        int epicInMemoryId = epicInMemory.getId();
        Subtask subtaskInMemory = taskManager.addSubTask(
                TestDataBuilder.buildSubtask("st", "d", epicInMemoryId));
        int subtaskInMemoryId = subtaskInMemory.getId();

        Subtask returnedSubtask = taskManager.getSubTaskById(subtaskInMemoryId);

        Assertions.assertAll(
                () -> assertNotNull(returnedSubtask, "Subtask was not returned."),
                () -> Assertions.assertEquals(subtaskInMemory, returnedSubtask, "Returned wrong subtask. ")
        );
    }

    @Test
    void getSubTaskByIdShouldReturnNullWhenIdIsNotValid() {
        Subtask returnedSubtask = taskManager.getSubTaskById(0);

        Assertions.assertNull(returnedSubtask, "Subtask was not returned.");
    }

    @Test
    void getSubTaskByIdShouldSaveSubtaskToHistory() {
        Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("e", "d"));
        int epicInMemoryId = epicInMemory.getId();
        Subtask subtaskInMemory = taskManager.addSubTask(
                TestDataBuilder.buildSubtask("st", "d", epicInMemoryId));
        int subtaskInMemoryId = subtaskInMemory.getId();
        List<Subtask> expected = List.of(subtaskInMemory);

        taskManager.getSubTaskById(subtaskInMemoryId);
        List<Task> actual = taskManager.getHistory();

        Assertions.assertIterableEquals(expected, actual, "Subtask was not added to the history.");
    }

    @Test
    void getSubtaskByInvalidIdShouldNotSaveSubtaskToHistory() {
        int subtaskId = 0;
        List<Subtask> expected = List.of();

        taskManager.getSubTaskById(subtaskId);
        List<Task> actual = taskManager.getHistory();

        Assertions.assertIterableEquals(expected, actual, " Null was added to the history.");
    }

    // addTask();
    @Test
    void addTaskShouldSaveTaskInTaskManager() {
        Task taskToAdd = TestDataBuilder.buildTask("task", "d");
        int expectedNumberOfTasks = taskManager.getAllTasks().size() + 1;

        Task task = taskManager.addTask(taskToAdd);
        int actualNumberOfTasks = taskManager.getAllTasks().size();

        Assertions.assertAll(
                () -> assertNotNull(taskManager.getTaskById(task.getId()),
                        "Task was not found."),
                () -> assertNotNull(taskManager.getAllTasks(), "Tasks are not returned."),
                () -> Assertions.assertEquals(expectedNumberOfTasks, actualNumberOfTasks,
                        "Incorrect number of tasks.")
        );
    }

    @Test
    void addedTaskRemainsUnchangedWhenAddedToTheTaskManager() {
        Task taskToAdd = TestDataBuilder.buildTask("task", "d");

        Task task = taskManager.addTask(taskToAdd);
        Task taskSaved = taskManager.getTaskById(task.getId());

        Assertions.assertAll(
                () -> assertNotNull(taskSaved, "Task was not saved."),
                () -> Assertions.assertEquals(taskToAdd.getName(), taskSaved.getName(),
                        "Title was changed."),
                () -> Assertions.assertEquals(taskToAdd.getDescription(), taskSaved.getDescription(),
                        "Description was changed."),
                () -> Assertions.assertEquals(taskToAdd.getStatus(), taskSaved.getStatus(),
                        "Status was changed.")
        );
    }

    @Test
    void addTaskShouldGenerateNewIdWhenSavingInTaskManagerTaskWithId() {
        Task taskInMemory = taskManager.addTask(TestDataBuilder.buildTask("t1", "d"));
        int taskInMemoryId = taskInMemory.getId();
        Task taskToAdd = TestDataBuilder.buildTask(taskInMemoryId, "t2", "d", TaskStatus.IN_PROGRESS);

        Throwable thrown = assertThrows(AlreadyExistsException.class, () -> {
            taskManager.addTask(taskToAdd);
        });

        Assertions.assertAll(
                () -> Assertions.assertNotNull(thrown.getMessage()),
                () -> Assertions.assertEquals(thrown.getMessage(), "object already exists")
        );
    }

    @Test
    void addTaskWithExistedInTaskManagerIdShouldNotUpdateTaskInMemory() {
        Task taskInMemory = taskManager.addTask(TestDataBuilder.buildTask("t1", "d"));
        int taskInMemoryId = taskInMemory.getId();
        Task taskToAdd = TestDataBuilder.buildTask(taskInMemoryId, "t2", "d", TaskStatus.IN_PROGRESS);

        Throwable thrown = assertThrows(AlreadyExistsException.class, () -> {
            taskManager.addTask(taskToAdd);
        });

        Assertions.assertAll(
                () -> Assertions.assertNotNull(thrown.getMessage()),
                () -> Assertions.assertEquals(thrown.getMessage(), "object already exists")
        );
    }

    @Test
    void addEpicShouldSaveEpicInTaskManager() {
        Epic epicToAdd = TestDataBuilder.buildEpic("epic", "d");
        int expectedNumberOfEpic = taskManager.getAllEpics().size() + 1;

        Epic epic = taskManager.addEpic(epicToAdd);
        int actualNumberOfEpics = taskManager.getAllEpics().size();

        Assertions.assertAll(
                () -> assertNotNull(taskManager.getEpicById(epic.getId()),
                        "Epic was not found."),
                () -> assertNotNull(taskManager.getAllEpics(), "Epics are not returned."),
                () -> Assertions.assertEquals(expectedNumberOfEpic, actualNumberOfEpics,
                        "Incorrect number of epics.")
        );
    }

    @Test
    void addedEpicRemainsUnchangedWhenAddedToTheTaskManager() {
        Epic epicToAdd = TestDataBuilder.buildEpic("epic", "d");

        taskManager.addEpic(epicToAdd);
        Task epicSaved = taskManager.getEpicById(epicToAdd.getId());

        Assertions.assertAll(
                () -> assertNotNull(epicSaved, "Epic was not saved"),
                () -> Assertions.assertEquals(epicToAdd.getName(), epicSaved.getName(),
                        "Title was changed."),
                () -> Assertions.assertEquals(epicToAdd.getDescription(),
                        epicSaved.getDescription(), "Description was changed.")
        );
    }

    @Test
    void addEpicShouldGenerateNewIdWhenSavingInTaskManagerEpicWithId() {
        Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("e1", "d"));
        int epicInMemoryId = epicInMemory.getId();
        Epic epicToAdd = TestDataBuilder.buildEpic(epicInMemoryId, "e2", "d");

        Throwable thrown = assertThrows(AlreadyExistsException.class, () -> {
            taskManager.addEpic(epicToAdd);
        });

        Assertions.assertAll(
                () -> Assertions.assertNotNull(thrown.getMessage()),
                () -> Assertions.assertEquals(thrown.getMessage(), "object already exists")
        );
    }

    @Test
    void addEpicWithExistedInTaskManagerIdShouldNotUpdateEpicInMemory() {
        Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("e1", "d"));
        int epicInMemoryId = epicInMemory.getId();
        Epic epicToAdd = TestDataBuilder.buildEpic(epicInMemoryId, "t2", "d");

        Throwable thrown = assertThrows(AlreadyExistsException.class, () -> {
            taskManager.addEpic(epicToAdd);
        });

        Assertions.assertAll(
                () -> Assertions.assertNotNull(thrown.getMessage()),
                () -> Assertions.assertEquals(thrown.getMessage(), "object already exists")
        );
    }

    @Test
    void EpicThrowsExceptionWhenAddingItselfAsSubtask() {
        Task epic = TestDataBuilder.buildEpic("Epic", "d");

        taskManager.addEpic((Epic) epic);

        assertThrows(ClassCastException.class, () -> {
            taskManager.addSubTask((Subtask) epic);
        });
    }

    @Test
    void addSubTaskShouldSaveSubtaskInTaskManager() {
        Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("e1", "d"));
        Subtask subtaskToAdd = TestDataBuilder.buildSubtask("task", "d", epicInMemory.getId());
        int expectedNumberOfSubtasks = taskManager.getAllSubtasks().size() + 1;

        Subtask savedSubtask = taskManager.addSubTask(subtaskToAdd);
        int actualNumberOfSubtasks = taskManager.getAllSubtasks().size();
        boolean subtaskExistInEpic = taskManager.getSubtasksByEpicId(savedSubtask.getEpicId())
                .contains(savedSubtask.getId());

        Assertions.assertAll(
                () -> assertNotNull(savedSubtask, "Subtask was not found."),
                () -> assertNotNull(taskManager.getAllSubtasks(), "Subtasks are not returned."),
                () -> Assertions.assertEquals(expectedNumberOfSubtasks, actualNumberOfSubtasks,
                        "Incorrect number of tasks."),
                () -> Assertions.assertTrue(subtaskExistInEpic, "Subtask was not saved inside epic.")
        );
    }

    @Test
    void addedSubtaskRemainsUnchangedWhenAddedToTheTaskManager() {
        Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("e1", "d"));
        Subtask subtaskToAdd = TestDataBuilder.buildSubtask("task", "d", epicInMemory.getId());

        taskManager.addSubTask(subtaskToAdd);
        Subtask savedSubtask = taskManager.getSubTaskById(subtaskToAdd.getId());

        Assertions.assertAll(
                () -> assertNotNull(savedSubtask, "Subtask was not saved."),
                () -> Assertions.assertEquals(subtaskToAdd.getName(), savedSubtask.getName(),
                        "Title was changed."),
                () -> Assertions.assertEquals(subtaskToAdd.getDescription(), savedSubtask.getDescription(),
                        "Description was changed."),
                () -> Assertions.assertEquals(subtaskToAdd.getStatus(), savedSubtask.getStatus(),
                        "Status was changed."),
                () -> Assertions.assertEquals(subtaskToAdd.getEpicId(), savedSubtask.getEpicId(),
                        "Corresponding epic was changed.")
        );
    }

    @Test
    void addSubTaskShouldGenerateNewIdWhenSavingInTaskManagerSubtaskWithId() {
        Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("e1", "d"));
        Subtask subtaskInMemory = taskManager.addSubTask(
                TestDataBuilder.buildSubtask("t1", "d", epicInMemory.getId()));
        int subtaskInMemoryId = subtaskInMemory.getId();
        Subtask subtaskToAdd = TestDataBuilder.buildSubtask(subtaskInMemoryId, "t2", "d",
                subtaskInMemory.getEpicId());
        int expectedNumberOfSubtasks = taskManager.getAllSubtasks().size() + 1;

        Throwable thrown = assertThrows(AlreadyExistsException.class, () -> {
            taskManager.addSubTask(subtaskToAdd);
        });

        Assertions.assertAll(
                () -> Assertions.assertNotNull(thrown.getMessage()),
                () -> Assertions.assertEquals(thrown.getMessage(), "Object already exists")
        );
    }

    @Test
    void addSubTaskWithExistedInTaskManagerIdShouldNotUpdateSubtaskInMemory() {
        Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("e1", "d"));
        Subtask subtaskInMemory = taskManager.addSubTask(
                TestDataBuilder.buildSubtask("st1", "d", epicInMemory.getId()));
        int subtaskInMemoryId = subtaskInMemory.getId();

        Subtask subtaskToAdd = TestDataBuilder.buildSubtask(subtaskInMemoryId, "st2", "d",
                epicInMemory.getId());

        Throwable thrown = assertThrows(AlreadyExistsException.class, () -> {
            taskManager.addSubTask(subtaskToAdd);
        });

        Assertions.assertAll(
                () -> Assertions.assertNotNull(thrown.getMessage()),
                () -> Assertions.assertEquals(thrown.getMessage(), "Object already exists")
        );
    }

    @Test
    void addSubTaskShouldNotAddSubTaskWithIncorrectEpicId() {
        Subtask subtaskToAdd = TestDataBuilder.buildSubtask("st1", "d", -1);

        Throwable thrown = assertThrows(EpicDoesntExistException.class, () -> {
            taskManager.addSubTask(subtaskToAdd);
        });

        Assertions.assertAll(
                () -> Assertions.assertNotNull(thrown.getMessage()),
                () -> Assertions.assertEquals(thrown.getMessage(), "Epic does not exist")
        );
    }

    @Test
    void subtaskShouldNotBeAbleBecomeItsOwnSubtask() {
        Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("Epic", "d"));
        Subtask subtaskInMemory = taskManager.addSubTask(
                TestDataBuilder.buildSubtask("Sb", "D", epicInMemory.getId()));
        Subtask subtaskToAdd = TestDataBuilder.buildCopySubtask(subtaskInMemory);
        subtaskToAdd.setEpicId(subtaskInMemory.getId());

        Throwable thrown = assertThrows(EpicDoesntExistException.class, () -> {
            taskManager.addSubTask(subtaskToAdd);
        });

        Assertions.assertAll(
                () -> Assertions.assertNotNull(thrown.getMessage()),
                () -> Assertions.assertEquals(thrown.getMessage(), "Epic does not exist")
        );
    }

    @Test
    void updateTaskShouldChangeStatusInMemory() {
        Task taskToAdd = TestDataBuilder.buildTask("task", "d");
        Task task = taskManager.addTask(taskToAdd);
        Task changes = TestDataBuilder.buildCopyTask(taskManager.getTaskById(task.getId()));
        changes.setStatus(TaskStatus.DONE);

        taskManager.updateTask(changes);
        TaskStatus actual = taskManager.getTaskById(task.getId()).getStatus();

        Assertions.assertEquals(TaskStatus.DONE, actual, "Status was not updated");
    }

    @Test
    void updateEpicShouldNotChangeStatusInMemory() {
        Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("e1", "d"));
        int epicInMemoryId = epicInMemory.getId();
        TaskStatus expected = epicInMemory.getStatus();
        Epic changes = TestDataBuilder.buildCopyEpic(taskManager.getEpicById(epicInMemoryId));
        changes.setStatus(TaskStatus.DONE);

        taskManager.updateEpic(changes);

        Assertions.assertEquals(expected, taskManager.getEpicById(epicInMemoryId).getStatus(),
                "Status of epic was updated manually");
    }

    @Test
    void updateSubtaskShouldChangeItsStatusInMemory() {
        Epic epicInMemory = taskManager.addEpic(TestDataBuilder.buildEpic("e1", "d"));
        int epicInMemoryId = epicInMemory.getId();
        Subtask subtaskInMemory = taskManager.addSubTask(
                TestDataBuilder.buildSubtask("st1", "d", epicInMemoryId));
        int subtaskInMemoryId = subtaskInMemory.getId();
        Subtask changes = TestDataBuilder.buildCopySubtask(
                taskManager.getSubTaskById(subtaskInMemoryId));
        changes.setStatus(TaskStatus.IN_PROGRESS);

        taskManager.updateSubTask(changes);
        Set<Integer> subtasksFromEpic = taskManager.getSubtasksByEpicId(epicInMemoryId);
        TaskStatus actualInEpic = taskManager.getSubTaskById(subtasksFromEpic.stream()
                .filter((st) -> st == subtaskInMemoryId).findFirst().get()).getStatus();

        Assertions.assertAll(
                () -> Assertions.assertEquals(TaskStatus.IN_PROGRESS,
                        taskManager.getSubTaskById(subtaskInMemoryId).getStatus()),
                () -> Assertions.assertEquals(TaskStatus.IN_PROGRESS, actualInEpic,
                        "Status inside Epic was not updated.")
        );

    }

    @Test
    void tasksInHistoryShouldKeepTheirStateAfterUpdatingThemInTaskManager() {
        Task taskInMemory = taskManager.addTask(TestDataBuilder.buildTask("t", "d"));
        int taskInMemoryId = taskInMemory.getId();
        taskManager.getTaskById(taskInMemoryId);
        Task expected = taskManager.getHistory().get(taskManager.getHistory().size() - 1);

        taskInMemory.setStatus(TaskStatus.DONE);
        taskManager.updateTask(taskInMemory);
        Task actual = taskManager.getHistory().get(taskManager.getHistory().size() - 1);

        Assertions.assertEquals(expected, actual, "Id is different");
        Assertions.assertEquals(expected.getName(), actual.getName(), "Id is different");
        Assertions.assertEquals(expected.getDescription(), actual.getDescription(),
                "Description is different");
        Assertions.assertEquals(expected.getStatus(), actual.getStatus(), "Status is different");
    }

    private void getHistoryReady() {
        final List<Task> tasks = TestDataBuilder.buildTasks();
        for (Task t : tasks) {
            if (t instanceof Epic) {
                taskManager.addEpic((Epic) t);
            } else if (t instanceof Subtask) {
                taskManager.addSubTask((Subtask) t);
            } else {
                taskManager.addTask(t);
            }
        }
        for (Task t : tasks) {
            if (t instanceof Epic) {
                taskManager.getEpicById(t.getId());
            } else if (t instanceof Subtask) {
                taskManager.getSubTaskById(t.getId());
            } else {
                taskManager.getTaskById(t.getId());
            }
        }
    }
}