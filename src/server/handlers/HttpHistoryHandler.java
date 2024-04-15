package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import server.enums.HttpCode;
import server.enums.HttpMethod;
import server.response.ErrorResponse;
import service.taskmanagers.TaskManager;

import java.io.IOException;

public class HttpHistoryHandler extends HttpRequestHandler {


    public HttpHistoryHandler(TaskManager taskManager, Gson gson) {
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
        writeResponse(httpExchange, HttpCode.SUCCESS.getCode(), taskManager.getHistoryAll());
    }

}