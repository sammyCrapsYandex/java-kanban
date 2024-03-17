import exception.AlreadyExistsException;
import exception.EpicDoesntExistException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.TaskManager;

public class Main {
    public static void main(String[] args) throws AlreadyExistsException, EpicDoesntExistException {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();
        taskManager.createTask(new Task("Помыть посуду", "тут должно быть описание", TaskManager.getId()));
        taskManager.createTask(new Task("Поспать", "когда-нибудь ты это сделаешь", TaskManager.getId()));

        Epic epicWithTwoTasks = new Epic("Переехать в новую квартиру", null, TaskManager.getId());
        taskManager.createEpic(epicWithTwoTasks);

        Subtask subtask4 = new Subtask("Выбрать бюджет", null, TaskManager.getId(), epicWithTwoTasks.getId());
        taskManager.createSubTask(subtask4);

        Subtask subtask5 = new Subtask("Найти квартиру на авито", "Требуется завести аккаунт", TaskManager.getId(), epicWithTwoTasks.getId());
        taskManager.createSubTask(subtask5);

        Epic epicWithOneTasks = new Epic("Стать разработчиком", "надо что-то делать", TaskManager.getId());
        taskManager.createEpic(epicWithOneTasks);
        Subtask subtask7 = new Subtask("Пройти курс в яндексе", "Не вылететь из своей кагорты", TaskManager.getId(), epicWithOneTasks.getId());
        taskManager.createSubTask(subtask7);

        System.out.println(taskManager);

        Subtask updateSubtask4 = new Subtask(
                subtask4.getName(),
                subtask4.getDescription(),
                subtask4.getId(),
                subtask4.getEpicId(),
                TaskStatus.IN_PROGRESS
        );

        taskManager.updateSubTask(updateSubtask4);
        System.out.println(taskManager);
        Subtask updateSubtask7 = new Subtask(
                subtask7.getName(),
                subtask7.getDescription(),
                subtask7.getId(),
                subtask7.getEpicId(),
                TaskStatus.DONE);
        taskManager.updateSubTask(updateSubtask7);

        System.out.println(taskManager);

        taskManager.createSubTask(new Subtask("Найти мотивацию", null, TaskManager.getId(), epicWithOneTasks.getId()));

        System.out.println(taskManager);

    }

}
