package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import entities.Epic;
import entities.SubTask;
import manager.TaskManager;
import server.validation.EpicValidator;

import java.io.IOException;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler {
    private final TaskManager manager;
    protected final Gson gson = createGson();

    public EpicsHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    protected void doGet(HttpExchange exchange, String path, String query) throws IOException {
        if (path.startsWith("/epics/") && path.contains("/subtasks")) {
            int epicId = Integer.parseInt(path.split("/")[2]);
            List<SubTask> subs = manager.getSubtasksEpic(epicId);
            if (subs != null) {
                sendText(exchange, gson.toJson(subs), HttpStatus.OK.getCode());
            } else {
                sendNotFound(exchange, "Эпик с id=" + epicId + " не найден");
            }
        } else if (query == null) {
            sendText(exchange, gson.toJson(manager.getEpics()), HttpStatus.OK.getCode());
        } else if (query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            Epic epic = manager.getEpic(id);
            if (epic != null) {
                sendText(exchange, gson.toJson(epic), HttpStatus.OK.getCode());
            } else {
                sendNotFound(exchange, "Эпик с id=" + id + " не найден");
            }
        } else {
            sendText(exchange, "Некорректный GET-запрос", HttpStatus.BAD_REQUEST.getCode());
        }
    }

    @Override
    protected void doPost(HttpExchange exchange, String path, String body) throws IOException {
        Epic epic = gson.fromJson(body, Epic.class);
        String err = EpicValidator.validate(epic);
        if (err != null) {
            sendText(exchange, err, HttpStatus.NOT_ACCEPTABLE.getCode());
            return;
        }
        if (epic.getId() == 0 || manager.getEpic(epic.getId()) == null) {
            manager.addEpic(epic);
            sendText(exchange, "Эпик создан", HttpStatus.CREATED.getCode());
        } else {
            sendText(exchange, "Нельзя создать эпик с существующим id", HttpStatus.BAD_REQUEST.getCode());
        }
    }

    @Override
    protected void doPut(HttpExchange exchange, String path, String body) throws IOException {
        Epic epic = gson.fromJson(body, Epic.class);
        if (manager.getEpic(epic.getId()) != null) {
            manager.updateEpic(epic);
            sendText(exchange, "Эпик обновлён", HttpStatus.OK.getCode());
        } else {
            sendNotFound(exchange, "Эпик с id=" + epic.getId() + " не найден");
        }
    }

    @Override
    protected void doDelete(HttpExchange exchange, String path, String query) throws IOException {
        if (query != null && query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            if (manager.getEpic(id) != null) {
                manager.removeEpic(id);
                sendText(exchange, "Эпик удалён", HttpStatus.OK.getCode());
            } else {
                sendNotFound(exchange, "Эпик с id=" + id + " не найден");
            }
        } else {
            sendText(exchange, "Не указан id", HttpStatus.BAD_REQUEST.getCode());
        }
    }
}