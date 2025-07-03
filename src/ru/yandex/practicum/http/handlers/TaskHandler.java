package ru.yandex.practicum.http.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.taskmanager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import ru.yandex.practicum.task.Task;

public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            Optional<Integer> idOpt = extractId(exchange.getRequestURI().getPath(), "/tasks");

            switch (method) {
                case "GET":
                    if (idOpt.isPresent()) {
                        Task task = taskManager.getTaskById(idOpt.get());
                        if (task == null) {
                            sendNotFound(exchange, "Задача не найдена");
                        } else {
                            sendJson(exchange, 200, task);
                        }
                    } else {
                        sendJson(exchange, 200, taskManager.getAllTasks());
                    }
                    break;

                case "POST":
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    System.out.println("Получен POST-запрос с телом: " + body);
                    if (body == null || body.isEmpty()) {
                        sendNotFound(exchange, "Тело запроса не может быть пустым");
                        break;
                    }
                    try {
                        Task task = gson.fromJson(body, Task.class);
                        if (task == null) {
                            sendNotFound(exchange, "Неверный формат задачи");
                            return;
                        }
                        if (task.getId() == 0) {
                            int createdTask = taskManager.createTask(task);
                            Map<String, Integer> response = Map.of("id", createdTask);
                            sendCreatedWithBody(exchange, response);
                        } else {
                            taskManager.updateTask(task);
                            sendJson(exchange, 200, task);
                        }
                    } catch (JsonSyntaxException e) {
                        sendServerError(exchange, "Неверный JSON: " + e.getMessage());
                    } catch (IllegalStateException e) {
                        sendHasIntersections(exchange, "Ошибка: " + e.getMessage());
                    } catch (Exception e) {
                        sendServerError(exchange, "Ошибка сервера: " + e.getMessage());
                    }
                    break;

                case "DELETE":
                    if (idOpt.isPresent()) {
                        taskManager.deleteTaskById(idOpt.get());
                        sendText(exchange, "Задача удалена.");
                    } else {
                        taskManager.deleteAllTasks();
                        sendText(exchange, "Все задачи удалены");
                    }
                    break;

                default:
                    sendNotFound(exchange, "Метод не поддерживается");
            }

        } catch (Exception e) {
            sendServerError(exchange, "Внутренняя ошибка сервера: " + e.getMessage());
        }
    }


}

