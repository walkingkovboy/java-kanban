package Model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<SubTask> subTasks;

    public Epic(Task task, ArrayList<SubTask> subTasks) {
        super(task.getTitle(), task.getDescription(), task.getStatus());
        this.subTasks = subTasks;
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(SubTask subTasks) {
        this.subTasks.add(subTasks);
    }
}
