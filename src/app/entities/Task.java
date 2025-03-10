package app.entities;

import app.enums.TaskStatus;
import app.enums.TaskType;
import app.utils.LocalDateAdapter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected int id;
    protected String title;
    protected String description;
    protected TaskStatus status = TaskStatus.NEW;
    protected LocalDateTime startTime;
    protected Duration duration;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Task(String title, String description, LocalDateTime startTime, Duration duration) {
        this(title, description);
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String title, String description, int id) {
        this(title, description);
        this.id = id;
    }

    public Task(String title, String description, int id, TaskStatus status, LocalDateTime startTime,
                Duration duration) {
        this(title, description, startTime, duration);
        this.id = id;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskType getTaskType() {
        return TaskType.TASK;
    }

    public Duration getDuration() {
        if (duration == null) return Duration.ZERO;
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getStartTimeString() {
        if (startTime == null) return null;
        return startTime.format(LocalDateAdapter.formatter);
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) return null;
        return startTime.plusMinutes(duration.toMinutes());
    }

    public String getEndTimeString() {
        LocalDateTime endTime = getEndTime();
        if (endTime == null) return null;
        return endTime.format(LocalDateAdapter.formatter);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Задача{" +
                "название='" + title + '\'' +
                ", описание='" + description + '\'' +
                ", id='" + id + '\'' +
                ", статус='" + status + '\'' +
                ", дата начала='" + getStartTimeString() + '\'' +
                ", продолжительность='" + getDuration().toMinutes() + '\'' +
                ", дата окончания='" + getEndTimeString() + '\'';
    }
}
