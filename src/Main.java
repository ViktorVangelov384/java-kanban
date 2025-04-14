import TaskManager.TaskManager;
import task.*;

public class Main {

    public static void main(String[] args) {
         TaskManager manager = new TaskManager();

         Task task1 = manager.createTask(new Task("Задача 1", "",
                 TaskStatus.NEW));
         Task task2 = manager.createTask(new Task("Задача 2", "",
                 TaskStatus.IN_PROGRESS));
         Epic epic1 = manager.createEpic(new Epic("Эпик 1", ""));
         Epic epic2 = manager.createEpic(new Epic("Эпик 2", ""));

         Subtask subtask1 = manager.createSubtask(new Subtask("Подзадача 1", "",
                 TaskStatus.NEW, epic1.getId()));
         Subtask subtask2 = manager.createSubtask(new Subtask("Подзадача 2", "",
                 TaskStatus.IN_PROGRESS, epic2.getId()));
         Subtask subtask3 = manager.createSubtask(new Subtask("Подзадача", "",
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

