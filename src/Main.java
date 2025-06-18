import ru.yandex.practicum.task.Task;
import ru.yandex.practicum.task.TaskStatus;
import ru.yandex.practicum.taskmanager.InMemoryTaskManager;
import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.Subtask;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        int task1 = manager.createTask(new Task("Задача 1", "Описание 1 задачи",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(1)));
        int task2 = manager.createTask(new Task("Задача 2", "Описание 2 задачи",
                TaskStatus.IN_PROGRESS, LocalDateTime.now().plusHours(1), Duration.ofMinutes(5)));
        int epic1 = manager.createEpic(new Epic("Эпик 1", "Описание 1 эпика"));
        int epic2 = manager.createEpic(new Epic("Эпик 2", "Описание 2 эпика"));

        int subtask1 = manager.createSubtask(new Subtask("Подзадача 1", "Описание 1 подзадачи",
                TaskStatus.NEW, epic1, LocalDateTime.now().plusHours(2), Duration.ofMinutes(20)));
        int subtask2 = manager.createSubtask(new Subtask("Подзадача 2", "Описание 2 подзадачи",
                TaskStatus.IN_PROGRESS, epic2, LocalDateTime.now().plusHours(3), Duration.ofMinutes(15)));
        int subtask3 = manager.createSubtask(new Subtask("Подзадача 3", "Описание 3 подзадачи",
                TaskStatus.DONE, epic2, LocalDateTime.now().plusHours(4), Duration.ofMinutes(20)));

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
        System.out.println("Проверка статусов");
        System.out.println("Статус 1 задачи " + manager.getTaskById(task1).getStatus());
        System.out.println("Статус 1 подзадачи " + manager.getSubtaskById(subtask1).getStatus());
        System.out.println("Статус 2 эпика " + manager.getEpicById(epic2).getStatus());

        System.out.println("Изменение статусов");
        Task taskUpdate = manager.getTaskById(task1);
        taskUpdate.setStatus(TaskStatus.DONE);
        manager.updateTask(taskUpdate);
        Subtask subtaskUpdate = manager.getSubtaskById(subtask1);
        subtaskUpdate.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtaskUpdate);

        System.out.println("1 задача после изменения:");
        System.out.println("Статус 1 задачи " +
                manager.getTaskById(task2).getStatus());
        System.out.println("Статус 1 подзадачи " +
                manager.getSubtaskById(subtask1).getStatus());
        System.out.println("Статус 1 эпика " +
                manager.getEpicById(epic1).getStatus());

        System.out.println("Удаление");
        System.out.println("Удаление 1 задачи и 2 эпика");
        manager.deleteTaskById(task1);
        manager.deleteEpicById(epic2);

        System.out.println("Остались задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Остались эпики:");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
        }
        System.out.println("Остались подзадачи:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }
    }
}


