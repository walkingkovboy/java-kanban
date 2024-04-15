package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import server.enums.HttpCode;
import server.enums.HttpMethod;
import server.response.ErrorResponse;
import service.taskmanagers.TaskManager;

import java.io.IOException;
import java.util.Optional;

public class HttpEpicSubtasksHandler extends HttpRequestHandler {
    public HttpEpicSubtasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleHttpExchange(HttpExchange httpExchange) throws IOException {
        String requestMethod = httpExchange.getRequestMethod();
        if (!HttpMethod.GET.name().equals(requestMethod)) {
            writeResponse(httpExchange, HttpCode.METHOD_NOT_ALLOWED.getCode(), new ErrorResponse("Доступен только метод GET."));
            return;
        }
        handleGetHttpExchange(httpExchange);
    }

    @Override
    protected void handleGetHttpExchange(HttpExchange httpExchange) throws IOException {
        Optional<String> query = Optional.ofNullable(httpExchange.getRequestURI().getQuery());
        Optional<Integer> epicId = Optional.ofNullable(Integer.parseInt(getParamFromQuery(query.get()).get("id")));
        if (!epicId.isPresent()) {
            writeResponse(httpExchange, HttpCode.BAD_REQUEST.getCode(), new ErrorResponse("Неправильно задан запрос."));
            return;
        }
        if (taskManager.getEpic(epicId.get()) == null) {
            writeResponse(httpExchange, HttpCode.BAD_REQUEST.getCode(), new ErrorResponse("Неправильно задан запрос. Эпика с таким ID не существует."));
            return;
        }
        writeResponse(httpExchange, HttpCode.SUCCESS.getCode(), taskManager.getSubTaskEpic(taskManager.getEpic(epicId.get())));
    }

}
