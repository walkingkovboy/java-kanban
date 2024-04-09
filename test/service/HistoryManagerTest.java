package service;

import model.Task;
import service.history.HistoryManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class HistoryManagerTest<T extends HistoryManager> {

    protected HistoryManager historyManager;
    Task task1;
    Task task2;
    Task task3;

    @DisplayName("Добавление в историю задачу")
    @Test
    void addShouldReturnNotEmptyListAfterTaskWasAdded() {
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();
        assertFalse(history.isEmpty());
    }
    @DisplayName("Если ничего не добавили история должна быть пустой")
    @Test
    void addShouldReturnEmptyListWhenTaskWereNotAdded() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }
    @DisplayName("Добавляем одну и ту же задачу в историю дважды, размер истории должен быть равен единице")
    @Test
    void addShouldNotWorkForDuplicate() {
        historyManager.add(task1);
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();
        assertEquals(1,history.size());
    }
    @DisplayName("Проверка на удалении первой задачи из истории, добавили три задачи, одну удалили, размер истории равен двум")
    @Test
    void removeFromTail() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(1);
        List<Task> history = historyManager.getHistory();
        assertEquals(2,history.size());
    }
    @DisplayName("Проверка на удалении второй задачи из истории, добавили три задачи, одну удалили, размер истории равен двум")
    @Test
    void removeFromMiddle() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(2);
        List<Task> history = historyManager.getHistory();
        assertEquals(2,history.size());
    }
    @DisplayName("Проверка на удалении последней задачи из истории, добавили три задачи, одну удалили, размер истории равен двум")
    @Test
    void removeFromHead() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(3);
        List<Task> history = historyManager.getHistory();
        assertEquals(2,history.size());
    }
    @DisplayName("Проверяем метод get при наличии элементов в истории")
    @Test
    void getHistory() {
        historyManager.add(task1);
        Collection<Task> taskList = Collections.singletonList(task1);
        List<Task> history = historyManager.getHistory();
        assertArrayEquals(taskList.toArray(), history.toArray());
    }
}
