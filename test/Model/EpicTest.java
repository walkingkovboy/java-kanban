package Model;

import Service.inMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private inMemoryTaskManager taskManager;
    private Epic epic;
    @BeforeEach
    public void setUp(){
        taskManager = new inMemoryTaskManager();
        epic = new Epic(new Task("NameEpic","EpicDescription",Status.NEW),new ArrayList<>());
        taskManager.createEpic(epic);
    }
    @DisplayName("Статус должен совпасть")
    @Test
    public void epicStatusNewWhenNoSubTasks(){
        assertEquals(Status.NEW,epic.getStatus());
    }
    @DisplayName("Статус должен быть NEW")
    @Test
    public void epicStatusNewWhenAllSubTasksNew(){
        SubTask subTask1 = new SubTask(new Task("SubTask1","Subtask1Description",Status.NEW),epic);
        SubTask subTask2 = new SubTask(new Task("SubTask2","Subtask2Description",Status.NEW),epic);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        assertEquals(Status.NEW,epic.getStatus());
    }
    @Test
    @DisplayName("Статус должен быть DONE")
    public void epicStatusDoneWhenAllSubTasksDone(){
        SubTask subTask1 = new SubTask(new Task("SubTask1","Subtask1Description",Status.DONE),epic);
        SubTask subTask2 = new SubTask(new Task("SubTask2","Subtask2Description",Status.DONE),epic);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        assertEquals(Status.DONE,epic.getStatus());
    }
    @Test
    @DisplayName("Статус должен быть IN_PROGRESS")
    public void epicStatusInProgressWhenSomeSubTasksDoneOrNew(){
        SubTask subTask1 = new SubTask(new Task("SubTask1","Subtask1Description",Status.DONE),epic);
        SubTask subTask2 = new SubTask(new Task("SubTask2","Subtask2Description",Status.IN_PROGRESS),epic);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        assertEquals(Status.IN_PROGRESS,epic.getStatus());
    }
    @Test
    @DisplayName("Статус должен быть IN_PROGRESS")
    public void eepicStatusInProgressWhenAllSubTasksInProgress(){
        SubTask subTask1 = new SubTask(new Task("SubTask1","Subtask1Description",Status.IN_PROGRESS),epic);
        SubTask subTask2 = new SubTask(new Task("SubTask2","Subtask2Description",Status.IN_PROGRESS),epic);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        assertEquals(Status.IN_PROGRESS,epic.getStatus());
    }

}