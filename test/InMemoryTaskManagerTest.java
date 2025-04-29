import manager.Managers;
import manager.TaskManager;

public class InMemoryTaskManagerTest extends TaskManagerTest {

    @Override
    protected TaskManager createTaskManager() {
        return Managers.getDefault(); // или new InMemoryTaskManager(), как хочешь
    }
}
