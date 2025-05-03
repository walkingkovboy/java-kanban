package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public HistoryHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if ("GET".equals(method)) {
            try {
                String response = gson.toJson(manager.getHistory());
                sendText(exchange, response, 200);
            } catch (Exception e) {
                System.out.println("Ошибка сериализации: " + e.getMessage());
                sendText(exchange, "Ошибка сериализации истории", 500);
            }
        } else {
            exchange.sendResponseHeaders(405, 0);
            exchange.close();
        }
    }
}