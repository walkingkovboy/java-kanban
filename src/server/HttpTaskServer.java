package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import server.handlers.*;
import service.taskmanagers.Manager;
import service.taskmanagers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Scanner;

public class HttpTaskServer {
    public HttpTaskServer() {
    }

    private final Integer port = 8080;
    private HttpServer httpServer;

    public void startTaskServer(TaskManager taskManager, Gson gson) throws IOException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(port), 0);
        httpServer.createContext("/prioritized", new HttpTasksHandler(taskManager, gson));
        httpServer.createContext("/history", new HttpHistoryHandler(taskManager, gson));
        httpServer.createContext("/task", new HttpTaskHandler(taskManager, gson));
        httpServer.createContext("/subtask", new HttpSubTaskHandler(taskManager, gson));
        // httpServer.createContext("/subtask/epic", new HttpEpicSubtasksHandler(taskManager, gson));
        httpServer.createContext("/epic", new HttpEpicHandler(taskManager, gson));
        httpServer.start();
        System.out.printf("Сервер запущен на %s порту", port);
    }

    public void stop() {
        httpServer.stop(0);
    }

    public Integer getPort() {
        return port;
    }

    public static void main(String[] args) {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        TaskManager taskManager = Manager.getDefaultTaskManager();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting().registerTypeAdapter(LocalDateTime.class, new LocalTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter()).serializeNulls();
        Gson gson = gsonBuilder.create();

        try {
            httpTaskServer.startTaskServer(taskManager, gson);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        Scanner in = new Scanner(System.in);
        System.out.println();
        System.out.println("Input enter for close server");
        in.nextLine();
        in.close();

        httpTaskServer.stop();
    }
}
