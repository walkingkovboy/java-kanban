package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskManager manager;
    protected final Gson gson = createGson();

    public HistoryHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    protected void doGet(HttpExchange exchange, String path, String query) throws IOException {
        try {
            sendText(exchange, gson.toJson(manager.getHistory()), HttpStatus.OK.getCode());
        } catch (Exception e) {
            sendText(exchange, "Ошибка сериализации истории: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.getCode());
        }
    }
}