import TaskManager.TaskManager;
import task.*;

public class Main {

    public static void main(String[] args) {
         TaskManager manager = new TaskManager();

         Task task1 = manager.createTask(new Task("Задача 1", "Описание 1 задачи",
                 TaskStatus.NEW));
         Task task2 = manager.createTask(new Task("Задача 2", "Описание 2 задачи",
                 TaskStatus.IN_PROGRESS));
         Epic epic1 = manager.createEpic(new Epic("Эпик 1", "Описание 1 эпика"));
         Epic epic2 = manager.createEpic(new Epic("Эпик 2", "Описание 2 эпика"));

         Subtask subtask1 = manager.createSubtask(new Subtask("Подзадача 1", "Описание 1 подзадачи",
                 TaskStatus.NEW, epic1.getId()));
         Subtask subtask2 = manager.createSubtask(new Subtask("Подзадача 2", "Описание 2 подзадачи",
                 TaskStatus.IN_PROGRESS, epic2.getId()));
         Subtask subtask3 = manager.createSubtask(new Subtask("Подзадача 3", "Описание 3 подзадачи",
                 TaskStatus.DONE, epic2.getId()));

        System.out.println("Все задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Все эпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
            System.out.println("все подзадачи:");
            for (Subtask subtask : manager.getAllSubtasks()) {
                System.out.println(subtask);
            }
        }
    }
}

