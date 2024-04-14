package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<SubTask> subTasks;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        subTasks = new ArrayList<>();
    }

    public void recalculateStartTimeAndEndTime() {
        LocalDateTime startTime = LocalDateTime.MIN;
        Duration duration = Duration.ofMinutes(0);
        for (SubTask subTask : subTasks) {
            if (subTask.getStartTime().isPresent()) {
                if (startTime.isBefore(subTask.getStartTime().get())) {
                    startTime = subTask.getStartTime().get();
                }
                duration = duration.plus(subTask.getDuration());
            }
        }
        if (!startTime.isEqual(LocalDateTime.MIN)) {
            setStartTime(startTime);
            setDuration(duration);
            setEndTime(startTime.plus(duration));
        }
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return this.getId() == epic.getId() && Objects.equals(this.getTitle(), epic.getTitle()) && Objects.equals(this.getDescription(), epic.getDescription()) && this.getStatus() == epic.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getTitle(), this.getDescription(), this.getId(), this.getStatus());
    }
}