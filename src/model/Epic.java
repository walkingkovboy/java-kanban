package model;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<SubTask> subTasks;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        subTasks = new ArrayList<>();
    }

    public Epic(String name, String description, Status status, int id) {
        super(name, description, status, id);
        subTasks = new ArrayList<>();
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(SubTask subTasks) {
        this.subTasks.add(subTasks);
    }

    @Override
    public String getType() {
        return "EPIC";
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s", this.getType(), this.getId(), this.getTitle(), this.getStatus(),
                this.getDescription());
    }
}