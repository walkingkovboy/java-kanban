package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

public class Task implements Comparable<Task> {
    private String title;
    private String description;
    private int id;
    private Status status;
    protected Optional<Duration> duration = Optional.empty();
    protected Optional<LocalDateTime> startTime = Optional.empty();

    public static final String IF_TIME_NOT_SET = "";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public Task(String title, String description, Status status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task(String title, String description, Status status, int id) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.id = id;
    }

    public Duration getDuration() {
        return duration.orElse(Duration.ofMinutes(0));
    }

    public void setDuration(Duration duration) {
        this.duration = Optional.of(duration);
    }

    public Optional<LocalDateTime> getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = Optional.of(startTime);
    }

    public LocalDateTime getEndTime() {
        return startTime.get().plus(duration.get());
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getType() {
        return "TASK";
    }

    @Override
    public String toString() {
        String startTimeStr = !startTime.isPresent() ? IF_TIME_NOT_SET : startTime.get().format(DATE_TIME_FORMATTER);
        return String.format("%s,%s,%s,%s,%s,%s,%s", this.getType(), id, title, status, description, startTimeStr, getDuration().toString());
    }

    @Override
    public int compareTo(Task o) {
        if (this.startTime.isPresent() && o.startTime.isPresent()) {
            return this.startTime.get().compareTo(o.startTime.get());
        } else {
            return 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(title, task.title) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, id, status);
    }
}
