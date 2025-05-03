package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import entities.SubTask;
import manager.TaskManager;
import manager.ValidationException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public SubtasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String method = exchange.getRequestMethod();

        try {
            if ("GET".equals(method)) {
                if (query == null) {
                    sendText(exchange, gson.toJson(manager.getSubtasks()), 200);
                } else if (query.startsWith("id=")) {
                    int id = Integer.parseInt(query.replaceFirst("id=", ""));
                    SubTask subTask = manager.getSubtask(id);
                    if (subTask != null) {
                        sendText(exchange, gson.toJson(subTask), 200);
                    } else {
                        sendNotFound(exchange, "Подзадача с id=" + id + " не найдена");
                    }
                } else {
                    sendText(exchange, "Некорректный GET-запрос", 400);
                }

            } else if ("POST".equals(method)) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                SubTask subTask = gson.fromJson(body, SubTask.class);
                if (manager.getEpic(subTask.getEpicId()) == null) {
                    sendNotFound(exchange, "Эпик с id=" + subTask.getEpicId() + " не найден");
                    return;
                }
                if (subTask.getId() != 0 && manager.getSubtask(subTask.getId()) != null) {
                    sendText(exchange, "Нельзя создать подзадачу с существующим id", 400);
                    return;
                }
                try {
                    manager.addSubtask(subTask, subTask.getEpicId());
                    sendText(exchange, "Подзадача создана", 201);
                } catch (ValidationException e) {
                    sendText(exchange, "Ошибка на сервере: " + e.getMessage(), 406);
                }
            } else if ("PUT".equals(method)) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                SubTask subTask = gson.fromJson(body, SubTask.class);
                if (manager.getSubtask(subTask.getId()) != null) {
                    try {
                        manager.updateSubtask(subTask);
                        sendText(exchange, "Подзадача обновлена", 200);
                    } catch (ValidationException e) {
                        sendText(exchange, "Ошибка на сервере: " + e.getMessage(), 406);
                    }
                } else {
                    sendNotFound(exchange, "Подзадача с id=" + subTask.getId() + " не найдена");
                }
            } else if ("DELETE".equals(method)) {
                if (query != null && query.startsWith("id=")) {
                    int id = Integer.parseInt(query.replaceFirst("id=", ""));
                    SubTask subTask = manager.getSubtask(id);
                    if (subTask != null) {
                        manager.removeSubtasks(id);
                        sendText(exchange, "Подзадача удалена", 200);
                    } else {
                        sendNotFound(exchange, "Подзадача с id=" + id + " не найдена");
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
