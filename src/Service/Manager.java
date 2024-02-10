package Service;

public class Manager {
    public static TaskManager getDefaultTaskManager() {
        return new inMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new inMemoryHistoryManager();
    }
}
