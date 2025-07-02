package ru.yandex.practicum.http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.taskmanager.TaskManager;
import ru.yandex.practicum.task.Subtask;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        super(gson);
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            Optional<Integer> idOpt = extractId(exchange.getRequestURI().getPath(), "/subtasks");

            switch (method) {
                case "GET":
                    if (idOpt.isPresent()) {
                        Subtask subtask = taskManager.getSubtaskById(idOpt.get());
                        if (subtask == null) {
                            sendNotFound(exchange, "Подзадача не найдена");
                        } else {
                            sendJson(exchange, 200, subtask);
                        }
                    } else {
                        sendJson(exchange, 200, taskManager.getAllSubtasks());
                    }
                    break;

                case "POST":
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Subtask subtask = gson.fromJson(body, Subtask.class);
                    try {
                        if (subtask.getId() == 0) {
                            int createdSubtask = taskManager.createSubtask(subtask);
                            Map<String, Integer> response = Map.of("id", createdSubtask);
                            sendCreatedWithBody(exchange, response);
                        } else {
                            taskManager.updateSubtask(subtask);
                            sendJson(exchange, 200, subtask);
                        }
                    } catch (IllegalStateException e) {
                        sendHasIntersections(exchange, "Подзадача пересекается с существующими задачами.");
                    }
                    break;
                case "DELETE":
                    if (idOpt.isPresent()) {
                        taskManager.deleteSubtask(idOpt.get());
                        sendText(exchange, "подзадача удалена");

                    } else {
                        taskManager.deleteAllSubtasks();
                        sendText(exchange, "Все подзадачи удалены");
                    }
                    break;

                default:
                    sendNotFound(exchange, "Метод не поддерживается");
            }

        } catch (Exception e) {
            sendServerError(exchange, "Ошибка сервера: " + e.getMessage());
        }
    }
}

