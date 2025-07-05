package ru.yandex.practicum.http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

import ru.yandex.practicum.taskmanager.TaskManager;

public class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendNotFound(exchange, "Метод не поддерживается");
                return;
            }

            sendJson(exchange, 200, taskManager.getPrioritizedTasks());

        } catch (Exception e) {
            sendServerError(exchange, "Ошибка сервера: " + e.getMessage());
        }
    }
}
