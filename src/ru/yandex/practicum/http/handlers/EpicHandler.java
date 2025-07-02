package ru.yandex.practicum.http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.taskmanager.TaskManager;
import ru.yandex.practicum.task.Epic;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    private TaskManager taskManager;

    public EpicHandler(TaskManager taskManager, Gson gson) {
        super(gson);
        this.taskManager = taskManager;

    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            Optional<Integer> idOpt = extractId(exchange.getRequestURI().getPath(), "/epics");

            switch (method) {
                case "GET":
                    if (idOpt.isPresent()) {
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
                    if (epic.getId() == 0) {
                        int createdEpic = taskManager.createEpic(epic);
                        Map<String, Integer> response = Map.of("id", createdEpic);
                        sendCreatedWithBody(exchange, response);
                    } else {
                        taskManager.updateEpic(epic);
                        sendJson(exchange, 200, epic);
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

