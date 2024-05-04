package service;

import builder.TestDataBuilder;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    private TestDataBuilder testDataBuilder;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        testDataBuilder = new TestDataBuilder();
    }

    @ParameterizedTest
    @MethodSource("provideDifferentTypesTasks")
    <T extends Task> void addShouldAcceptTaskEpicSubtaskTypesForSaving(T task) {
        final List<Task> expectedHistory = new ArrayList<>(Collections.singletonList(task));
        historyManager.add(task);

        final List<Task> actualHistory = historyManager.getHistory();

        Assertions.assertIterableEquals(expectedHistory, actualHistory,
                "Returned list should contain the same task.");
    }

    @Test
    void addShouldNotSaveNullToTheHistoryWhenTaskIsNull() {
        Task nullTask = null;
        final int expectedHistorySize = historyManager.getHistory().size();

        historyManager.add(nullTask);
        final int actualHistorySize = historyManager.getHistory().size();

        Assertions.assertEquals(expectedHistorySize, actualHistorySize,
                "Should not save null to the history");
    }

    @Test
    void shouldKeepViewingTaskOrderFromOldToNew() {
        List<Task> expectedTasks = testDataBuilder.buildTasks();
        for (Task task : expectedTasks) {
            historyManager.add(task);
        }

        final List<Task> actual = historyManager.getHistory();

        Assertions.assertIterableEquals(expectedTasks, actual, "Order of elements should be same.");
    }

    @Test
    void addShouldReplaceExistedTaskWithANewVersionAndMoveItToTheEndOfTheList() {
        fillUpHistoryManager();
        int expectedHistorySize = historyManager.getHistory().size();
        int expectedPosition = historyManager.getHistory().size() - 1;
        Task taskToAdd = testDataBuilder.buildCopyTask(historyManager.getHistory().get(1));

        taskToAdd.setStatus(TaskStatus.DONE);

        historyManager.add(taskToAdd);
        int actualHistorySize = historyManager.getHistory().size();
        final Task actualTask = historyManager.getHistory().get(expectedPosition);

        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedHistorySize, actualHistorySize,
                        "Size of history should not expand."),
                () -> Assertions.assertEquals(taskToAdd, actualTask, "Tasks should be same"),
                () -> Assertions.assertEquals(taskToAdd.getName(), actualTask.getName(),
                        "Titles should be same."),
                () -> Assertions.assertEquals(taskToAdd.getDescription(), actualTask.getDescription(),
                        "Descriptions should be same."),
                () -> Assertions.assertEquals(taskToAdd.getStatus(), actualTask.getStatus(),
                        "Statuses should be same.")
        );
    }

    @ParameterizedTest
    @MethodSource("provideDeletionPositions")
    void removeShouldDeleteTaskFromDifferentPositionsOfTheHistoryList(int positionToDelete,
                                                                      int shiftForExpected, int shiftForActual, String deletionPosition) {
        fillUpHistoryManager();
        int expectedHistorySize = historyManager.getHistory().size() - 1;
        final Task taskToDelete = historyManager.getHistory().get(positionToDelete);
        final Task expectedMiddleElement = historyManager.getHistory()
                .get(positionToDelete + shiftForExpected);

        historyManager.remove(taskToDelete.getId());
        final Task actualMiddleElement = historyManager.getHistory()
                .get(positionToDelete + shiftForActual);
        int actualHistorySize = historyManager.getHistory().size();
        boolean isDeleted = !historyManager.getHistory().contains(taskToDelete);

        Assertions.assertAll(
                () -> Assertions.assertTrue(isDeleted, "Task was not removed."),
                () -> Assertions.assertEquals(expectedHistorySize, actualHistorySize,
                        "History size should reduce by 1."),
                () -> Assertions.assertEquals(expectedMiddleElement, actualMiddleElement,
                        "Incorrect " + deletionPosition + " element in the history.")
        );
    }

    @Test
    void removeShouldDoNothingWhenIdIsNotExistInTheHistory() {
        fillUpHistoryManager();
        int expectedHistorySize = historyManager.getHistory().size();
        int idToDelete = 187;
        final Task taskToDelete = testDataBuilder.buildTask(idToDelete, "t", "d", TaskStatus.NEW);
        boolean isNotValidId = !historyManager.getHistory().contains(taskToDelete);

        historyManager.remove(idToDelete);
        boolean isNotSavedInHistory = !historyManager.getHistory().contains(taskToDelete);
        int actualHistorySize = historyManager.getHistory().size();

        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedHistorySize, actualHistorySize,
                        "History size should not change."),
                () -> Assertions.assertEquals(isNotValidId, isNotSavedInHistory,
                        "Remove should mot save tasks.")
        );
    }

    @Test
    void shouldReturnEmptyList() {
        int expectedHistorySize = 0;

        final List<Task> actualHistory = historyManager.getHistory();
        int actualHistorySize = actualHistory.size();

        Assertions.assertEquals(expectedHistorySize, actualHistorySize, "Should be empty list.");
    }

    private static Stream<Arguments> provideDeletionPositions() {
        List<Task> tasks =TestDataBuilder.buildTasks();
        return Stream.of(
                Arguments.of(0, 1, 0, "beginning"),
                Arguments.of((tasks.size() - 1) / 2, 1, 0, "middle"),
                Arguments.of(tasks.size() - 1, -1, -1, "end")
        );
    }

    private static List<Task> provideDifferentTypesTasks() {
        Task task = TestDataBuilder.buildTask(1, "task", "d", TaskStatus.NEW);
        Epic epic = TestDataBuilder.buildEpic(2, "epic", "description");
        Subtask subtask = TestDataBuilder.buildSubtask(3, "subtask", "notes", 2);
        return new ArrayList<>(Arrays.asList(task, epic, subtask));
    }

    private void fillUpHistoryManager() {
        List<Task> tasks = testDataBuilder.buildTasks();
        for (Task task : tasks) {
            historyManager.add(task);
        }
    }
}