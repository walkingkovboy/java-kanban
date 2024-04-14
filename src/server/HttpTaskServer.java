package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import server.handlers.*;
import service.taskmanagers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    public HttpTaskServer() {
    }

    private static final Integer PORT = 8080;
    private static HttpServer httpServer;

    public void startTaskServer(TaskManager taskManager, Gson gson) throws IOException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new HttpTasksHandler(taskManager, gson));
        httpServer.createContext("/tasks/history", new HttpHistoryHandler(taskManager, gson));
        httpServer.createContext("/tasks/task", new HttpTaskHandler(taskManager, gson));
        httpServer.createContext("/tasks/subtask", new HttpSubTaskHandler(taskManager, gson));
        httpServer.createContext("/tasks/subtask/epic", new HttpEpicSubtasksHandler(taskManager, gson));
        httpServer.createContext("/tasks/epic", new HttpEpicHandler(taskManager, gson));
        httpServer.start();
        System.out.printf("Сервер запущен на %s порту", PORT);
    }

    public static void stop() {
        httpServer.stop(0);
    }

    public static Integer getPort() {
        return PORT;
    }
}
