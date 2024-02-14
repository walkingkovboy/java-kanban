package Model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private ArrayList<SubTask> subTasks;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        subTasks = new ArrayList<>();
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(SubTask subTasks) {
        this.subTasks.add(subTasks);
    }
}
