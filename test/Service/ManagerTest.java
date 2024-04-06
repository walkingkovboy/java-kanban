package Service;

import Model.Status;
import Model.Task;
import Service.taskmanagers.Manager;
import Service.taskmanagers.TaskManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
    @DisplayName("При неправильном айди получаем NUL ")
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