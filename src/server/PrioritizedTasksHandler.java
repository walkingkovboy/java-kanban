package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;

public class PrioritizedTasksHandler extends BaseHttpHandler {
    private final TaskManager manager;
    protected final Gson gson = createGson();

    public PrioritizedTasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    protected void doGet(HttpExchange exchange, String path, String query) throws IOException {
        sendText(exchange, gson.toJson(manager.getPrioritizedTasks()), HttpStatus.OK.getCode());
    }
}
