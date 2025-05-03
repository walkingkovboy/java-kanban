import entities.Epic;
import entities.Status;
import entities.Task;
import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private TaskManager taskManager;
    private Epic epic;
    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        historyManager = taskManager.getHistoryManager();
        epic = new Epic("Эпик1", "Описание первого эпика");
        task1 = new Task("Задача 1", "Описание 1 задачи", Status.DONE);
        task2 = new Task("Задача 2", "Описание 2 задачи", Status.IN_PROGRESS);
        taskManager.addEpic(epic);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
    }

    @DisplayName("Проверка добавления в историю")
    @Test
    void testHistoryAdd() {
        taskManager.getEpic(0);
        taskManager.getTask(1);
        taskManager.getTask(2);
        assertEquals(3, historyManager.getHistory().size());
    }

    @DisplayName("Проверка дубликатов")
    @Test
    void testHistoryNoDuplicat() {
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
    void testHistoryOrder() {
        taskManager.getEpic(0);
        taskManager.getTask(1);
        taskManager.getTask(2);
        taskManager.getTask(1);
        assertEquals(epic, historyManager.getHistory().get(0));
        assertEquals(task2, historyManager.getHistory().get(1));
        assertEquals(task1, historyManager.getHistory().get(2));
    }

    @DisplayName("Проверка на удаление задач")
    @Test
    void testHistoryDeleteTask() {
        taskManager.getEpic(0);
        taskManager.getTask(1);
        taskManager.getTask(2);
        taskManager.getTask(1);
        taskManager.removeTask(1);
        taskManager.removeTask(2);
        taskManager.removeEpic(0);
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @DisplayName("Проверка на удаления одного элемента")
    @Test
    void testHistoryDeleteTaskMiddle() {
        taskManager.getEpic(0);
        taskManager.getTask(1);
        taskManager.getTask(2);
        taskManager.getTask(1);
        taskManager.removeTask(2);
        assertEquals(2, historyManager.getHistory().size());
        assertEquals(epic, historyManager.getHistory().get(0));
        assertEquals(task1, historyManager.getHistory().get(1));

    }
}
