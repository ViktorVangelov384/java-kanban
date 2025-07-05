package ru.yandex.practicum.httptest;

import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import com.google.gson.Gson;
import ru.yandex.practicum.http.HttpTaskServer;
import ru.yandex.practicum.http.adapters.DurationAdapter;
import ru.yandex.practicum.http.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.task.*;
import ru.yandex.practicum.taskmanager.Managers;
import ru.yandex.practicum.taskmanager.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    private static HttpTaskServer taskServer;
    private static TaskManager manager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final String BASE_URL = "http://localhost:8080";

    @BeforeAll
    public static void setup() throws IOException {
        manager = Managers.getDefault();
        taskServer = new HttpTaskServer(manager);
        taskServer.start();
    }

    @AfterAll
    public static void tearDown() {
        taskServer.stop();
    }

    @BeforeEach
    public void resetManager() {
        manager.deleteAllTasks();
        manager.deleteAllEpics();
        manager.deleteAllSubtasks();

    }

    @Test
    public void testTaskEndpoints() throws Exception {
        Gson testGson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        Task task = new Task("Задача", "Описание", TaskStatus.NEW, LocalDateTime.now(),
                Duration.ofMinutes(30));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertFalse(response.body().isEmpty());

        List<Task> tasks = manager.getAllTasks();
        assertEquals(1, tasks.size());
        assertEquals("Задача", tasks.get(0).getName());
    }

    @Test
    public void testUpdateTask() throws Exception {
        Task task = new Task("Задача", "Описание", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(15));
        task.setId(1);
        manager.createTask(task);

        Task updated = new Task("Обновленная задача", "Описание", TaskStatus.IN_PROGRESS,
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(45));
        updated.setId(1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(updated)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task fromManager = manager.getTaskById(1);
        assertEquals("Обновленная задача", fromManager.getName());
        assertEquals(TaskStatus.IN_PROGRESS, fromManager.getStatus());
    }

    @Test
    public void testInvalidTaskCreation() throws Exception {
        String invalidJson = "{invalid json}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(invalidJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(500, response.statusCode());
    }

    @Test
    public void testNotFoundEndpoint() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/nonexistent"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }


    @Test
    public void testGetPrioritizedTasks() throws Exception {
        Task task1 = new Task("Задача 1", "Описание", TaskStatus.NEW,
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(30));
        Task task2 = new Task("Задача 2", "Описание", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(15));

        manager.createTask(task1);
        manager.createTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/prioritized"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> prioritized = manager.getPrioritizedTasks();
        assertEquals(2, prioritized.size());
        assertEquals("Задача 2", prioritized.get(0).getName());
    }


}
