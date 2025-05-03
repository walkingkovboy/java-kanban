package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import entities.Task;
import manager.TaskManager;
import manager.ValidationException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public TasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String method = exchange.getRequestMethod();

        try {
            if ("GET".equals(method)) {
                if (query == null) {
                    String response = gson.toJson(manager.getTasks());
                    sendText(exchange, response, 200);
                } else if (query.startsWith("id=")) {
                    int id = Integer.parseInt(query.replaceFirst("id=", ""));
                    Task task = manager.getTask(id);
                    if (task != null) {
                        String response = gson.toJson(task);
                        sendText(exchange, response, 200);
                    } else {
                        sendNotFound(exchange, "Задача с id=" + id + " не найдена");
                    }
                } else {
                    sendText(exchange, "Некорректный GET-запрос", 400);
                }

            } else if ("POST".equals(method)) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Task task = gson.fromJson(body, Task.class);
                try {
                    if (task.getId() == 0 || manager.getTask(task.getId()) == null) {
                        manager.addTask(task);
                        sendText(exchange, "Задача создана", 201);
                    } else {
                        sendText(exchange, "Нельзя создать задачу с существующим id", 400);
                    }
                } catch (ValidationException e) {
                    sendText(exchange, "Ошибка на сервере: " + e.getMessage(), 406);
                }

            } else if ("PUT".equals(method)) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Task task = gson.fromJson(body, Task.class);
                if (manager.getTask(task.getId()) != null) {
                    manager.updateTask(task);
                    sendText(exchange, "Задача обновлена", 200);
                } else {
                    sendNotFound(exchange, "Задача с id=" + task.getId() + " не найдена");
                }
            } else if ("DELETE".equals(method)) {
                if (query != null && query.startsWith("id=")) {
                    int id = Integer.parseInt(query.replaceFirst("id=", ""));
                    Task task = manager.getTask(id);
                    if (task != null) {
                        manager.removeTask(id);
                        sendText(exchange, "Задача удалена", 200);
                    } else {
                        sendNotFound(exchange, "Задача с id=" + id + " не найдена");
                    }
                } else {
                    sendText(exchange, "Не указан id", 400);
                }

            } else {
                exchange.sendResponseHeaders(405, 0);
                exchange.close();
            }

        } catch (Exception e) {
            sendText(exchange, "Ошибка на сервере: " + e.getMessage(), 500);
        }
    }
}
