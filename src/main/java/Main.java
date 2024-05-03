//import model.Epic;
//import model.Subtask;
//import model.Task;
//import model.TaskStatus;
//import service.InMemoryTaskManager;
//
//public class Main {
//    public static void main(String[] args) {
//        System.out.println("Поехали!");
//        InMemoryTaskManager taskManager = new InMemoryTaskManager(historyManager);
//        taskManager.createTask(new Task("Помыть посуду", "тут должно быть описание", InMemoryTaskManager.getId()));
//        taskManager.createTask(new Task("Поспать", "когда-нибудь ты это сделаешь", InMemoryTaskManager.getId()));
//
//        Epic epicWithTwoTasks = new Epic("Переехать в новую квартиру", null, InMemoryTaskManager.getId());
//        taskManager.createEpic(epicWithTwoTasks);
//
//        Subtask subtask4 = new Subtask("Выбрать бюджет", null, InMemoryTaskManager.getId(), epicWithTwoTasks.getId());
//        taskManager.createSubTask(subtask4);
//
//        Subtask subtask5 = new Subtask("Найти квартиру на авито", "Требуется завести аккаунт", InMemoryTaskManager.getId(), epicWithTwoTasks.getId());
//        taskManager.createSubTask(subtask5);
//
//        Epic epicWithOneTasks = new Epic("Стать разработчиком", "надо что-то делать", InMemoryTaskManager.getId());
//        taskManager.createEpic(epicWithOneTasks);
//        Subtask subtask7 = new Subtask("Пройти курс в яндексе", "Не вылететь из своей кагорты", InMemoryTaskManager.getId(), epicWithOneTasks.getId());
//        taskManager.createSubTask(subtask7);
//
//        System.out.println(taskManager);
//
//        Subtask updateSubtask4 = new Subtask(
//                subtask4.getName(),
//                subtask4.getDescription(),
//                subtask4.getId(),
//                subtask4.getEpicId(),
//                TaskStatus.IN_PROGRESS
//        );
//
//        taskManager.updateSubTask(updateSubtask4);
//        System.out.println(taskManager);
//        Subtask updateSubtask7 = new Subtask(
//                subtask7.getName(),
//                subtask7.getDescription(),
//                subtask7.getId(),
//                subtask7.getEpicId(),
//                TaskStatus.DONE);
//        taskManager.updateSubTask(updateSubtask7);
//
//        System.out.println(taskManager);
//
//        taskManager.createSubTask(new Subtask("Найти мотивацию", null, InMemoryTaskManager.getId(), epicWithOneTasks.getId()));
//
//        System.out.println(taskManager);
//
//    }
//
//}
