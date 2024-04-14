package service.taskmanagers;

import service.history.HistoryManager;
import service.history.InMemoryHistoryManager;

import java.io.IOException;

public class Manager {
    private static final String defaultURL = "http://localhost";
    public static TaskManager getDefaultTaskManager() {
        return new InMemoryTaskManager();
    }
    public static TaskManager getDefaultHttpManager() throws IOException, InterruptedException {
        return new HttpTaskManager(defaultURL);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
