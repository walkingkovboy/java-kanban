package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import entities.Task;
import manager.TaskManager;
import manager.ValidationException;
import server.validation.TaskValidator;

import java.io.IOException;

public class TasksHandler extends BaseHttpHandler {
    private final TaskManager manager;
    protected final Gson gson = createGson();

    public TasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    protected void doGet(HttpExchange exchange, String path, String query) throws IOException {
        if (query == null) {
            sendText(exchange, gson.toJson(manager.getTasks()), HttpStatus.OK.getCode());
        } else if (query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            Task task = manager.getTask(id);
            if (task != null) {
                sendText(exchange, gson.toJson(task), HttpStatus.OK.getCode());
            } else {
                sendNotFound(exchange, "Задача с id=" + id + " не найдена");
            }
        } else {
            sendText(exchange, "Некорректный GET-запрос", HttpStatus.BAD_REQUEST.getCode());
        }
    }

    @Override
    protected void doPost(HttpExchange exchange, String path, String body) throws IOException {
        Task task = gson.fromJson(body, Task.class);
        String err = TaskValidator.validate(task);
        if (err != null) {
            sendText(exchange, err, HttpStatus.NOT_ACCEPTABLE.getCode());
            return;
        }
        try {
            if (task.getId() == 0 || manager.getTask(task.getId()) == null) {
                manager.addTask(task);
                sendText(exchange, "Задача создана", HttpStatus.CREATED.getCode());
            } else {
                sendText(exchange, "Нельзя создать задачу с существующим id", HttpStatus.BAD_REQUEST.getCode());
            }
        } catch (ValidationException e) {
            sendText(exchange, "Ошибка на сервере: " + e.getMessage(), HttpStatus.NOT_ACCEPTABLE.getCode());
        }
    }

    @Override
    protected void doPut(HttpExchange exchange, String path, String body) throws IOException {
        Task task = gson.fromJson(body, Task.class);
        String err = TaskValidator.validate(task);
        if (err != null) {
            sendText(exchange, err, HttpStatus.NOT_ACCEPTABLE.getCode());
            return;
        }
        if (manager.getTask(task.getId()) != null) {
            manager.updateTask(task);
            sendText(exchange, "Задача обновлена", HttpStatus.OK.getCode());
        } else {
            sendNotFound(exchange, "Задача с id=" + task.getId() + " не найдена");
        }
    }

    @Override
    protected void doDelete(HttpExchange exchange, String path, String query) throws IOException {
        if (query != null && query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            if (manager.getTask(id) != null) {
                manager.removeTask(id);
                sendText(exchange, "Задача удалена", HttpStatus.OK.getCode());
            } else {
                sendNotFound(exchange, "Задача с id=" + id + " не найдена");
            }
        } else {
            sendText(exchange, "Не указан id", HttpStatus.BAD_REQUEST.getCode());
        }
    }
}