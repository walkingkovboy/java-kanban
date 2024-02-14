package Service;

import Model.Epic;
import Model.Status;
import Model.SubTask;
import Model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class inMemoryHistoryManagerTest extends ManagerTest {
    Epic epic;
    SubTask subTask;
    TaskManager tm = Manager.getDefaultTaskManager();

    @BeforeEach
    public void setUp() {
        epic = new Epic("NameEpic", "EpicDescription");
        subTask = new SubTask("SubTask1", "Subtask1Description", Status.NEW);
        epic = tm.createEpic(epic);
        subTask = tm.createSubTask(subTask, 0);
    }

    @Test
    @DisplayName("Проверка увелечения размера истории, при обращении к задачам (5 вызовов)")
    void getSubTaskAndGetEpicForSaveHistory() {
        tm.getEpic(0);
        tm.getSubTask(1);
        tm.getSubTask(1);
        tm.getSubTask(1);
        tm.getEpic(0);
        assertEquals(5, tm.getAll().size());
    }

    @Test
    @DisplayName("Проверка увелечения размера истории, при обращении к задачам (10 вызовов)")
    void getSubTaskAndGetEpicForSaveHistoryFull() {
        tm.getEpic(0);
        tm.getSubTask(1);
        tm.getSubTask(1);
        tm.getSubTask(1);
        tm.getEpic(0);
        tm.getEpic(0);
        tm.getSubTask(1);
        tm.getSubTask(1);
        tm.getSubTask(1);
        tm.getEpic(0);
        assertEquals(10, tm.getAll().size());
    }

    @Test
    @DisplayName("Проверка сохранения размера истории, при обращении к задачам (12 вызовов)")
    void getSubTaskAndGetEpicForSaveHistoryAfterTwelveСalls() {
        tm.getEpic(0);
        tm.getSubTask(1);
        tm.getSubTask(1);
        tm.getSubTask(1);
        tm.getEpic(0);
        tm.getEpic(0);
        tm.getSubTask(1);
        tm.getSubTask(1);
        tm.getSubTask(1);
        tm.getEpic(0);
        tm.getSubTask(1);
        tm.getEpic(0);
        assertEquals(10, tm.getAll().size());
    }

    @Test
    @DisplayName("Сравним первый элемент в истории после 12 вызовов")
    void compareTheFirstElementInTheHistoryAfterCalls() {
        tm.getEpic(0);
        tm.getSubTask(1);
        tm.getSubTask(1);
        tm.getSubTask(1);
        tm.getEpic(0);
        tm.getEpic(0);
        tm.getSubTask(1);
        tm.getSubTask(1);
        tm.getSubTask(1);
        tm.getEpic(0);
        tm.getSubTask(1);
        tm.getEpic(0);
        assertEquals(tm.getAll().get(0), tm.getSubTask(1));
    }

}