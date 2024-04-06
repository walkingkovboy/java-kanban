package service.taskmanagers;

import service.history.HistoryManager;
import service.history.InMemoryHistoryManager;

public class Manager {
    public static TaskManager getDefaultTaskManager() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
