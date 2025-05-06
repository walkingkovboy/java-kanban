package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.adapter.DurationAdapter;
import server.adapter.LocalDateTimeAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class BaseHttpHandler implements HttpHandler {
    protected static final String GET = "GET";
    protected static final String POST = "POST";
    protected static final String PUT = "PUT";
    protected static final String DELETE = "DELETE";

    @Override
    public final void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String query = exchange.getRequestURI().getQuery();
        String body = null;
        if (POST.equals(method) || PUT.equals(method)) {
            try (InputStream requestBodyStream = exchange.getRequestBody()) {
                body = new String(requestBodyStream.readAllBytes(), StandardCharsets.UTF_8);
            }
        }

        try {
            switch (method) {
                case GET:
                    doGet(exchange, path, query);
                    break;
                case POST:
                    doPost(exchange, path, body);
                    break;
                case PUT:
                    doPut(exchange, path, body);
                    break;
                case DELETE:
                    doDelete(exchange, path, query);
                    break;
                default:
                    exchange.sendResponseHeaders(HttpStatus.METHOD_NOT_ALLOWED.getCode(), 0);
                    exchange.close();
            }
        } catch (Exception e) {
            sendText(exchange, "Ошибка на сервере: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.getCode());
        }
    }

    private void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(HttpStatus.METHOD_NOT_ALLOWED.getCode(), 0);
        exchange.close();
    }

    protected void doGet(HttpExchange exchange, String path, String query) throws IOException {
        sendMethodNotAllowed(exchange);
    }

    protected void doPost(HttpExchange exchange, String path, String body) throws IOException {
        sendMethodNotAllowed(exchange);
    }

    protected void doPut(HttpExchange exchange, String path, String body) throws IOException {
        sendMethodNotAllowed(exchange);
    }

    protected void doDelete(HttpExchange exchange, String path, String query) throws IOException {
        sendMethodNotAllowed(exchange);
    }

    protected void sendText(HttpExchange exchange, String text, int code) throws IOException {
        if (text == null) {
            text = "[]";
        }
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        try (HttpExchange ex = exchange) {
            ex.sendResponseHeaders(code, resp.length);
            ex.getResponseBody().write(resp);
        }
        System.out.println(
                ">>> sendText: code=" + code + ", length=" + resp.length +
                        ", text=" + (text.length() > 200 ? text.substring(0, 200) + "..." : text)
        );
    }

    protected void sendNotFound(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, message, HttpStatus.NOT_FOUND.getCode());
    }

    protected static Gson createGson() {
        return new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }
}