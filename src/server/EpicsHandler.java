package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import entities.Epic;
import entities.SubTask;
import manager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public EpicsHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if ("GET".equals(method)) {
                if (path.startsWith("/epics/") && path.contains("/subtasks")) {
                    String[] pathParts = path.split("/");
                    int epicId = Integer.parseInt(pathParts[2]);
                    List<SubTask> subTasks = manager.getSubtasksEpic(epicId);
                    if (subTasks != null) {
                        String response = gson.toJson(subTasks);
                        sendText(exchange, response, 200);
                    } else {
                        sendNotFound(exchange, "Эпик с id=" + epicId + " не найден");
                    }
                } else if (query == null) {
                    List<Epic> epics = manager.getEpics();
                    String response = gson.toJson(epics);
                    sendText(exchange, response, 200);
                } else if (query.startsWith("id=")) {
                    int id = Integer.parseInt(query.replaceFirst("id=", ""));
                    Epic epic = manager.getEpic(id);
                    if (epic != null) {
                        String response = gson.toJson(epic);
                        sendText(exchange, response, 200);
                    } else {
                        sendNotFound(exchange, "Эпик с id=" + id + " не найден");
                    }
                } else {
                    sendText(exchange, "Некорректный GET-запрос", 400);
                }

            } else if ("POST".equals(method)) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Epic epic = gson.fromJson(body, Epic.class);
                if (epic.getId() == 0 || manager.getEpic(epic.getId()) == null) {
                    manager.addEpic(epic);
                    sendText(exchange, "Эпик создан", 201);
                } else {
                    sendText(exchange, "Нельзя создать эпик с существующим id", 400);
                }

            } else if ("PUT".equals(method)) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Epic epic = gson.fromJson(body, Epic.class);
                if (manager.getEpic(epic.getId()) != null) {
                    manager.updateEpic(epic);
                    sendText(exchange, "Эпик обновлён", 200);
                } else {
                    sendNotFound(exchange, "Эпик с id=" + epic.getId() + " не найден");
                }
            } else if ("DELETE".equals(method)) {
                if (query != null && query.startsWith("id=")) {
                    int id = Integer.parseInt(query.replaceFirst("id=", ""));
                    Epic epic = manager.getEpic(id);
                    if (epic != null) {
                        manager.removeEpic(id);
                        sendText(exchange, "Эпик удалён", 200);
                    } else {
                        sendNotFound(exchange, "Эпик с id=" + id + " не найден");
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

