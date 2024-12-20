package entities;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<SubTask> subTasks;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        this.subTasks = new ArrayList<>();
    }

    public Epic(String name, String description, int id) {
        super(name, description, id);
    }


    public void setSubTasks(SubTask subTask) {
        for (SubTask sbtask : subTasks) {
            if (sbtask.getId() == subTask.getId()) {
                this.subTasks.set(this.subTasks.indexOf(sbtask), subTask);
                return;
            }
        }
        subTasks.add(subTask);
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void removeAllSubtasks() {
        this.subTasks.clear();
    }

    public void removeSubtask(SubTask subTask) {
        this.subTasks.remove(subTask);
    }

    @Override
    public String toString() {
        String result = "Epic{" +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
        if (subTasks != null) {
            result = result + ", subTasks.length=" + subTasks.size();
        } else {
            result = result + ", subTasks.length=null";
        }
        return result + "}";
    }
}
