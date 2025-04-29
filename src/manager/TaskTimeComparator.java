package manager;

import entities.Task;

import java.util.Comparator;

public class TaskTimeComparator implements Comparator<Task> {
    @Override
    public int compare(Task t1, Task t2) {
        if (t1.getStartTime() == null && t2.getStartTime() == null) {
            return Integer.compare(t1.getId(), t2.getId());
        }
        if (t1.getStartTime() == null) {
            return 1;
        }
        if (t2.getStartTime() == null) {
            return -1;
        }
        int result = t1.getStartTime().compareTo(t2.getStartTime());
        if (result == 0) {
            return Integer.compare(t1.getId(), t2.getId());
        }
        return result;
    }
}
