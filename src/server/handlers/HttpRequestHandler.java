package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.enums.HttpCode;
import server.response.ErrorResponse;
import service.taskmanagers.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static server.enums.HttpCode.INTERNAL_SERVER_ERROR;

public abstract class HttpRequestHandler implements HttpHandler {
    protected final TaskManager taskManager;
    protected Gson gson;

    public HttpRequestHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            handleHttpExchange(httpExchange);
        } catch (Exception ex) {
            writeResponse(httpExchange, INTERNAL_SERVER_ERROR.getCode(), new ErrorResponse(ex.getMessage()));
        }
    }

    protected void handleHttpExchange(HttpExchange httpExchange) throws IOException {
        writeResponse(httpExchange, HttpCode.METHOD_NOT_ALLOWED.getCode(), new ErrorResponse("Метод не поддерживается"));
    }

    protected void handleGetHttpExchange(HttpExchange httpExchange) throws IOException {
        writeResponse(httpExchange, HttpCode.METHOD_NOT_ALLOWED.getCode(), new ErrorResponse("Метод не поддерживается"));
    }

    protected void handlePostHttpExchange(HttpExchange httpExchange) throws IOException {
        writeResponse(httpExchange, HttpCode.METHOD_NOT_ALLOWED.getCode(), new ErrorResponse("Метод не поддерживается"));
    }

    protected void handleDeleteHttpExchange(HttpExchange httpExchange) throws IOException {
        writeResponse(httpExchange, HttpCode.METHOD_NOT_ALLOWED.getCode(), new ErrorResponse("Метод не поддерживается"));
    }

    protected <T> void writeResponse(HttpExchange httpExchange, int code, T response) throws IOException {
        String json = gson.toJson(response);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        Headers headers = httpExchange.getResponseHeaders();
        headers.set("Content-type", "application/json");
        httpExchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream outputStream = httpExchange.getResponseBody()) {
            outputStream.write(bytes);
        } finally {
            httpExchange.close();
        }
    }

    protected Map<String, String> getParamFromQuery(String query) {
        return Arrays.stream(query.split("&"))
                .collect(Collectors.toMap(s -> s.split("=")[0], s -> s.split("=")[1]));
    }

}
