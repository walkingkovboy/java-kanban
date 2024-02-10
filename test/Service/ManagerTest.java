package Service;

import Model.Epic;
import Model.Status;
import Model.SubTask;
import Model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ManagerTest {
    TaskManager taskManager = Manager.getDefaultTaskManager();

    @Test
    @DisplayName("Проверка наличия таска после добавления")
    void getTaskShouldReturnTask1WhenItWereAdded() {
        Task task1 = taskManager.createTask(new Task("Task1", "Task1Description", Status.NEW));
        assertEquals(task1, taskManager.getTask(0));
    }

    @Test
    @DisplayName("При неправильном айди получаем NULL ")
    void getTaskShouldReturnNullWhenIdIsIncorrect() {
        Task task1 = taskManager.createTask(new Task("Task1", "Task1Description", Status.NEW));
        assertNull(taskManager.getTask(4));
    }

    @Test
    @DisplayName("Обращаемся в пустую хешмапу ")
    void getTaskShouldReturnNullWhenNoTasksInIt() {
        assertNull(taskManager.getTask(1));
    }

    @Test
    @DisplayName("Обращаемся в мапу после удаления ")
    void getTaskShouldReturnNullWhenRemoveTasks() {
        Task task1 = taskManager.createTask(new Task("Task1", "Task1Description", Status.NEW));
        taskManager.removeTask(task1.getId());
        assertNull(taskManager.getTask(1));
    }
}