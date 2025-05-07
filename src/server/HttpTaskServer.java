package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import server.adapter.DurationAdapter;
import server.adapter.LocalDateTimeAdapter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager manager;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TasksHandler(manager));
        server.createContext("/epics", new EpicsHandler(manager));
        server.createContext("/subtasks", new SubtasksHandler(manager));
        server.createContext("/history", new HistoryHandler(manager));
        server.createContext("/prioritized", new PrioritizedTasksHandler(manager));
        System.out.println("HTTP-сервер запущен на порту \uD83D\uDE0A " + PORT);
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    public static void main(String[] args) throws IOException {
        TaskManager manager = Managers.getDefault();
        HttpTaskServer httpServer = new HttpTaskServer(manager);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Завершение работы сервера...");
            httpServer.stop();
            System.out.println("Сервер остановлен. \uD83D\uDE1E ");
        }));
        httpServer.start();
    }
}

