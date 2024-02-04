package Service;

public class Manager {
    public TaskManager getDefaultTaskManager() {
        return new inMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new inMemoryHistoryManager();
    }
}
