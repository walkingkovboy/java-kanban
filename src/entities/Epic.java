package entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<SubTask> subTasks = new ArrayList<>(); // без transient

    private LocalDateTime endTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subTasks, epic.subTasks);
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTasks);
    }

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public Epic(String name, String description, int id) {
        super(name, description, id);
    }


    public void setSubTasks(SubTask subTask) {
        if (subTasks == null) {
            subTasks = new ArrayList<>();
        }
        for (SubTask sbtask : subTasks) {
            if (sbtask.getId() == subTask.getId()) {
                this.subTasks.set(this.subTasks.indexOf(sbtask), subTask);
                return;
            }
        }
        subTasks.add(subTask);
    }

    public ArrayList<SubTask> getSubTasks() {
        if (subTasks == null) {
            subTasks = new ArrayList<>();
        }
        return subTasks;
    }

    public void setSubTasks(ArrayList<SubTask> subTasks) {
        this.subTasks = subTasks;
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
