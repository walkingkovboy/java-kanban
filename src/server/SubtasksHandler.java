package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import entities.SubTask;
import manager.TaskManager;
import manager.ValidationException;
import server.validation.SubTaskValidator;

import java.io.IOException;

public class SubtasksHandler extends BaseHttpHandler {
    private final TaskManager manager;
    protected final Gson gson = createGson();

    public SubtasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    protected void doGet(HttpExchange exchange, String path, String query) throws IOException {
        if (query == null) {
            sendText(exchange, gson.toJson(manager.getSubtasks()), HttpStatus.OK.getCode());
        } else if (query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            SubTask subTask = manager.getSubtask(id);
            if (subTask != null) {
                sendText(exchange, gson.toJson(subTask), HttpStatus.OK.getCode());
            } else {
                sendNotFound(exchange, "Подзадача с id=" + id + " не найдена");
            }
        } else {
            sendText(exchange, "Некорректный GET-запрос", HttpStatus.BAD_REQUEST.getCode());
        }
    }

    @Override
    protected void doPost(HttpExchange exchange, String path, String body) throws IOException {
        SubTask subTask = gson.fromJson(body, SubTask.class);
        String err = SubTaskValidator.validate(subTask);
        if (err != null) {
            sendText(exchange, err, HttpStatus.NOT_ACCEPTABLE.getCode());
            return;
        }
        if (manager.getEpic(subTask.getEpicId()) == null) {
            sendNotFound(exchange, "Эпик с id=" + subTask.getEpicId() + " не найден");
            return;
        }
        if (subTask.getId() != 0 && manager.getSubtask(subTask.getId()) != null) {
            sendText(exchange, "Нельзя создать подзадачу с существующим id", HttpStatus.BAD_REQUEST.getCode());
            return;
        }
        try {
            manager.addSubtask(subTask, subTask.getEpicId());
            sendText(exchange, "Подзадача создана", HttpStatus.CREATED.getCode());
        } catch (ValidationException e) {
            sendText(exchange, "Ошибка на сервере: " + e.getMessage(), HttpStatus.NOT_ACCEPTABLE.getCode());
        }
    }

    @Override
    protected void doPut(HttpExchange exchange, String path, String body) throws IOException {
        SubTask subTask = gson.fromJson(body, SubTask.class);
        String err = SubTaskValidator.validate(subTask);
        if (err != null) {
            sendText(exchange, err, HttpStatus.NOT_ACCEPTABLE.getCode());
            return;
        }
        if (manager.getSubtask(subTask.getId()) != null) {
            try {
                manager.updateSubtask(subTask);
                sendText(exchange, "Подзадача обновлена", HttpStatus.OK.getCode());
            } catch (ValidationException e) {
                sendText(exchange, "Ошибка на сервере: " + e.getMessage(), HttpStatus.NOT_ACCEPTABLE.getCode());
            }
        } else {
            sendNotFound(exchange, "Подзадача с id=" + subTask.getId() + " не найдена");
        }
    }

    @Override
    protected void doDelete(HttpExchange exchange, String path, String query) throws IOException {
        if (query != null && query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            if (manager.getSubtask(id) != null) {
                manager.removeSubtasks(id);
                sendText(exchange, "Подзадача удалена", HttpStatus.OK.getCode());
            } else {
                sendNotFound(exchange, "Подзадача с id=" + id + " не найдена");
            }
        } else {
            sendText(exchange, "Не указан id", HttpStatus.BAD_REQUEST.getCode());
        }
    }
}