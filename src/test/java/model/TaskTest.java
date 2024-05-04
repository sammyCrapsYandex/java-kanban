package model;

import builder.TestDataBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskTest {

    private TestDataBuilder testDataBuilder;

    @BeforeEach
    void setUp() {
        testDataBuilder = new TestDataBuilder();
    }

    @Test
    void verifyTasksAreEqualsWhenIdsAreEquals() {
        Task task1 = testDataBuilder.buildTask(1, "Task1", "Description1", TaskStatus.NEW);
        Task task2 = testDataBuilder.buildTask(task1.getId(), "a", "s", TaskStatus.IN_PROGRESS);

        Assertions.assertEquals(task1, task2,
                "Tasks with the same ID are expected to be equal, but they are not.");
    }

    @Test
    void verifyTasksAreNotEqualsWhenIdsAreDifferentAndFieldsAreSame() {
        Task task1 = testDataBuilder.buildTask(1, "Task1", "Description1", TaskStatus.NEW);
        Task task2 = testDataBuilder.buildTask(3, task1.getName(), task1.getDescription(),
                task1.getStatus());

        Assertions.assertNotEquals(task1, task2,
                "Tasks with different ID are expected to be not equal, but they are.");
    }
}