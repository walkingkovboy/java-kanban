package Service;

import Model.Task;

import java.util.ArrayList;
import java.util.List;

public class inMemoryHistoryManager implements HistoryManager {
    private List<Task> viewhitory = new ArrayList<>(10);

    @Override
    public void addTaskHistory(Task task) {
        if (task == null) {
            return;
        }
        if (viewhitory.size() > 9) {
            viewhitory.remove(0);
        }
        viewhitory.add(task);
    }

    @Override
    public List<Task> getAll() {
        return List.copyOf(viewhitory);
    }
}
