package entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<SubTask> subTasks = new ArrayList<>();
    private LocalDateTime endTime;


    @Override
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    @Override
    public Status getStatus() {
        if (this.getSubTasks().isEmpty()) {
            this.setStatus(Status.NEW);
            return super.getStatus();
        }
        long countStatusNew = this.getSubTasks().stream()
                .filter(subTask -> subTask.getStatus() == Status.NEW)
                .count();

        long countStatusDone = this.getSubTasks().stream()
                .filter(subTask -> subTask.getStatus() == Status.DONE)
                .count();
        if (countStatusNew == this.getSubTasks().size()) {
            this.setStatus(Status.NEW);
        } else if (countStatusDone == this.getSubTasks().size()) {
            this.setStatus(Status.DONE);
        } else {
            this.setStatus(Status.IN_PROGRESS);
        }
        return super.getStatus();
    }

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public Epic(String name, String description, int id) {
        super(name, description, id);
    }


    public void setSubTasks(SubTask subTask) {
        for (int i = 0; i < subTasks.size(); i++) {
            if (subTasks.get(i).getId() == subTask.getId()) {
                subTasks.set(i, subTask);
                return;
            }
        }
        subTasks.add(subTask);
    }


    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<SubTask> subTasks) {
        this.subTasks = new ArrayList<>(subTasks);
    }

    public void removeAllSubtasks() {
        this.subTasks.clear();
    }

    public void removeSubtask(SubTask subTask) {
        this.subTasks.remove(subTask);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", subTasksCount=" + (subTasks != null ? subTasks.size() : 0) +
                ", duration=" + (getDuration() != null ? getDuration().toMinutes() + " min" : "null") +
                ", startTime=" + (getStartTime() != null ? getStartTime() : "null") +
                ", endTime=" + (getEndTime() != null ? getEndTime() : "null") +
                '}';
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
