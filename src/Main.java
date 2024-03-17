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
        Task task1 = taskManager.createTask("Помыть посуду", "тут должно быть описание");
        Task task2 = taskManager.createTask("Поспать", "когда-нибудь ты это сделаешь");

        Epic epic3 = taskManager.createEpic("Переехать в новую квартиру", null);
        Subtask subtask4 = taskManager.createSubTask("Выбрать бюджет", null, epic3.getId());
        Subtask subtask5 = taskManager.createSubTask("Найти квартиру на авито", "Требуется завести аккаунт", epic3.getId());

        Epic epic6 = taskManager.createEpic("Стать разработчиком", "надо что-то делать");
        Subtask subtask7 = taskManager.createSubTask("Пройти курс в яндексе", "Не вылететь из своей кагорты", epic6.getId());

        System.out.println(taskManager);

        Subtask updateSubtask4 = new Subtask(
                subtask4.getName(),
                subtask4.getDescription(),
                subtask4.getId(),
                subtask4.getEpicId(),
                TaskStatus.IN_PROGRESS);
        taskManager.updateSubTask(updateSubtask4);
        Subtask updateSubtask7 = new Subtask(
                subtask7.getName(),
                subtask7.getDescription(),
                subtask7.getId(),
                subtask7.getEpicId(),
                TaskStatus.DONE);
        taskManager.updateSubTask(updateSubtask7);
        System.out.println(taskManager);

        Subtask subtask8 = taskManager.createSubTask("НАйти мотивацию", null, epic6.getId());
        System.out.println(taskManager);
    }

}
