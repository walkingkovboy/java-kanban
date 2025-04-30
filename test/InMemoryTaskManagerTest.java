import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryTaskManagerTest extends TaskManagerTest {

    @Override
    protected TaskManager createTaskManager() {
        return Managers.getDefault();
    }
    @DisplayName("Проверка метода getDefault")
    @Test
    void testinMemoryTaskManagerIsManagerDefault() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        assertEquals(taskManager.getClass(), inMemoryTaskManager.getClass());
    }
}
