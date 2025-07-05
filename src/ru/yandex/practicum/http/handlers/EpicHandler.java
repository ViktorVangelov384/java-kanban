package ru.yandex.practicum.http.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import ru.yandex.practicum.taskmanager.TaskManager;
import ru.yandex.practicum.task.Epic;

public class EpicHandler extends BaseHttpHandler {

    public EpicHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            Optional<Integer> idOpt = extractId(exchange.getRequestURI().getPath(), "/epics");

            switch (method) {
                case "GET":
                    if (path.endsWith("/subtasks") && idOpt.isPresent()) {
                        sendJson(exchange, 200, taskManager.getSubtaskByEpicId(idOpt.get()));
                    } else if (idOpt.isPresent()) {
                        Epic epic = taskManager.getEpicById(idOpt.get());
                        if (epic == null) {
                            sendNotFound(exchange, "Эпик не найден");
                        } else {
                            sendJson(exchange, 200, epic);
                        }
                    } else {
                        sendJson(exchange, 200, taskManager.getAllEpics());
                    }
                    break;

                case "POST":
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Epic epic = gson.fromJson(body, Epic.class);
                    if (body == null || body.isEmpty()) {
                        sendNotFound(exchange, "Тело запроса не может быть пустым");
                        break;
                    }

                    try {
                        if (epic == null) {
                            sendNotFound(exchange, "Неверный формат эпика");
                            break;
                        }

                        if (epic.getId() == 0) {
                            int createdEpic = taskManager.createEpic(epic);
                            Map<String, Integer> response = Map.of("id", createdEpic);
                            sendCreatedWithBody(exchange, response);
                        } else {
                            sendNotFound(exchange, "Обновление эпиков не предусмотрено");
                        }
                    } catch (JsonSyntaxException e) {
                        sendServerError(exchange, "Неверный JSON: " + e.getMessage());
                    }
                    break;

                case "DELETE":
                    if (idOpt.isPresent()) {
                        taskManager.deleteEpicById(idOpt.get());
                        sendText(exchange, "Эпик удален");
                    } else {
                        taskManager.deleteAllEpics();
                        sendText(exchange, "Все эпики удалены");
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

