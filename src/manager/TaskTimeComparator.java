package manager;

import entities.Task;

import java.util.Comparator;

public class TaskTimeComparator implements Comparator<Task> {
    @Override
    public int compare(Task task, Task taskToCompare) {
        if (task.getStartTime() == null && taskToCompare.getStartTime() == null) {
            return Integer.compare(task.getId(), taskToCompare.getId());
        }
        if (task.getStartTime() == null) {
            return 1;
        }
        if (taskToCompare.getStartTime() == null) {
            return -1;
        }
        int result = task.getStartTime().compareTo(taskToCompare.getStartTime());
        if (result == 0) {
            return Integer.compare(task.getId(), taskToCompare.getId());
        }
        return result;
    }
}
