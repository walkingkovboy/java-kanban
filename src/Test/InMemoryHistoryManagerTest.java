package Test;

import Manager.HistoryManager;
import Manager.Managers;
import Manager.TaskManager;
import entities.Epic;
import entities.Status;
import entities.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        historyManager = taskManager.getHistoryManager();
    }

    @DisplayName("Проверка добавления в историю")
    @Test
    void testHystoryAdd() {
        Epic epic = new Epic("Эпик1", "Описание первого эпика");
        taskManager.addEpic(epic);
        taskManager.addTask(new Task("Задача 1", "Описание 1 задачи", Status.DONE));
        taskManager.addTask(new Task("Задача 2", "Описание 2 задачи", Status.IN_PROGRESS));
        taskManager.getEpic(0);
        taskManager.getTask(1);
        taskManager.getTask(2);
        assertEquals(3, historyManager.getHistory().size());
    }

    @DisplayName("Проверка дубликатов")
    @Test
    void testHystoryNoDuplicat() {
        Epic epic = new Epic("Эпик1", "Описание первого эпика");
        taskManager.addEpic(epic);
        taskManager.addTask(new Task("Задача 1", "Описание 1 задачи", Status.DONE));
        taskManager.addTask(new Task("Задача 2", "Описание 2 задачи", Status.IN_PROGRESS));
        taskManager.getEpic(0);
        taskManager.getEpic(0);
        taskManager.getEpic(0);
        taskManager.getTask(1);
        taskManager.getTask(2);
        taskManager.getTask(2);
        taskManager.getTask(2);
        taskManager.getTask(2);
        taskManager.getTask(2);
        taskManager.getTask(2);
        taskManager.getTask(2);
        taskManager.getTask(2);
        assertEquals(3, historyManager.getHistory().size());
    }

    @DisplayName("Проверка порядка задач в истории")
    @Test
    void testHystoryOrder() {
        Epic epic = new Epic("Эпик1", "Описание первого эпика");
        taskManager.addEpic(epic);
        Task task1 = new Task("Задача 1", "Описание 1 задачи", Status.DONE);
        Task task2 = new Task("Задача 2", "Описание 2 задачи", Status.IN_PROGRESS);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.getEpic(0);
        taskManager.getTask(1);
        taskManager.getTask(2);
        taskManager.getTask(1);
        assertEquals(epic, historyManager.getHistory().get(0));
        assertEquals(task2, historyManager.getHistory().get(1));
        assertEquals(task1, historyManager.getHistory().get(2));
    }
}
