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
    inMemoryTaskManager tm = new inMemoryTaskManager();
    @BeforeEach
    public void setUp() {

        epic = tm.createEpic(new Epic(new Task("Эпик", "Первый эпик", Status.NEW), new ArrayList<SubTask>()));
        subTask = tm.createSubTask(new SubTask(new Task("Подзадача", "Первая подзадача", Status.NEW), epic));
    }
    @Test
    @DisplayName("Проверка увелечения размера истории, при обращении к задачам (5 вызовов)")
    void getSubTaskAndGetEpicForSaveHistory(){
        tm.getEpic(0);
        tm.getSubTask(1);
        tm.getSubTask(1);
        tm.getSubTask(1);
        tm.getEpic(0);
        assertEquals(5, tm.historyManager.getAll().size());
    }
    @Test
    @DisplayName("Проверка увелечения размера истории, при обращении к задачам (10 вызовов)")
    void getSubTaskAndGetEpicForSaveHistoryFull(){
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
        assertEquals(10, tm.historyManager.getAll().size());
    }
    @Test
    @DisplayName("Проверка сохранения размера истории, при обращении к задачам (12 вызовов)")
    void getSubTaskAndGetEpicForSaveHistoryAfterTwelveСalls(){
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
        assertEquals(10, tm.historyManager.getAll().size());
    }
    @Test
    @DisplayName("Сравним первый элемент в истории после 12 вызовов")
    void compareTheFirstElementInTheHistoryAfterCalls(){
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
        assertEquals(tm.historyManager.getAll().get(0), tm.getSubTask(1));
    }

}