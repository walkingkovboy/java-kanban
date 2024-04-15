package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import model.Task;
import server.enums.HttpCode;
import server.enums.HttpMethod;
import server.response.ErrorResponse;
import server.response.SuccessResponse;
import service.taskmanagers.TaskManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

public class HttpTaskHandler extends HttpRequestHandler {

    public HttpTaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleHttpExchange(HttpExchange httpExchange) throws IOException {
        String requestMethod = httpExchange.getRequestMethod();
        if (HttpMethod.GET.name().equals(requestMethod)) {
            handleGetHttpExchange(httpExchange);
            return;
        }
        if (HttpMethod.POST.name().equals(requestMethod)) {
            handlePostHttpExchange(httpExchange);
            return;
        }
        if (HttpMethod.DELETE.name().equals(requestMethod)) {
            handleDeleteHttpExchange(httpExchange);
            return;
        }
        writeResponse(httpExchange, HttpCode.METHOD_NOT_ALLOWED.getCode(), new ErrorResponse("Доступны только методы GET, POST, DELETE"));
    }

    @Override
    protected void handleGetHttpExchange(HttpExchange httpExchange) throws IOException {
        Optional<String> query = Optional.ofNullable(httpExchange.getRequestURI().getQuery());
        //Если query пустой, значит строка запроса вида tasks/task, соответственно возвращаем все таски
        if (query.isEmpty()) {
            writeResponse(httpExchange, HttpCode.SUCCESS.getCode(), taskManager.getTasksAll());
            return;
        }
        Optional<Integer> taskId = Optional.ofNullable(Integer.parseInt(getParamFromQuery(query.get()).get("id")));
        if (taskId.isEmpty()) {
            writeResponse(httpExchange, HttpCode.BAD_REQUEST.getCode(), new ErrorResponse("Неправильно задан запрос."));
            return;
        }
        Optional<Task> task = Optional.ofNullable(taskManager.getTask(taskId.get()));
        if (task.isPresent()) {
            writeResponse(httpExchange, HttpCode.SUCCESS.getCode(), task.get());
        } else {
            writeResponse(httpExchange, HttpCode.BAD_REQUEST.getCode(), new ErrorResponse("Задачи с таким идентификатором не сущесвует"));
        }
    }

    @Override
    protected void handlePostHttpExchange(HttpExchange httpExchange) throws IOException {
        try (InputStream inputStream = httpExchange.getRequestBody();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            Task task = gson.fromJson(bufferedReader, Task.class);
            if (taskManager.addTaskServer(task)) {
                writeResponse(httpExchange, HttpCode.CREATED.getCode(), new SuccessResponse("Задача успешно добавлена!"));
            } else {
                writeResponse(httpExchange, HttpCode.BAD_REQUEST.getCode(), new ErrorResponse("Не удалось добавить задачу. Проверьте запрос."));
            }
        }
    }

    @Override
    protected void handleDeleteHttpExchange(HttpExchange httpExchange) throws IOException {
        Optional<String> query = Optional.ofNullable(httpExchange.getRequestURI().getQuery());
        if (query.isEmpty()) {
            taskManager.removeAllTasks();
            writeResponse(httpExchange, HttpCode.SUCCESS.getCode(), new SuccessResponse("Задачи успешно удалены!"));
            return;
        }
        Optional<Integer> taskId = Optional.ofNullable(Integer.parseInt(getParamFromQuery(query.get()).get("id")));
        if (taskId.isEmpty()) {
            writeResponse(httpExchange, HttpCode.BAD_REQUEST.getCode(), new ErrorResponse("Неправильно задан запрос."));
            return;
        }
        if (taskManager.removeTask(taskId.get())) {
            writeResponse(httpExchange, HttpCode.SUCCESS.getCode(), new SuccessResponse(String.format("Задача с id %s успешно удалена", taskId.get())));
        } else {
            writeResponse(httpExchange, HttpCode.BAD_REQUEST.getCode(), new ErrorResponse("Задачи с таким идентификатором не сущесвует"));
        }
    }

}

