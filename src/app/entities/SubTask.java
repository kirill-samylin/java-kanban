package app.entities;

import app.enums.TaskStatus;
import app.enums.TaskType;

import java.time.LocalDateTime;

public class SubTask extends Task {
    private int epicId;
    private LocalDateTime endTime;

    public SubTask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public SubTask(String title, String description, int id, int epicId) {
        super(title, description, id);
        this.epicId = epicId;
    }

    public SubTask(String title, String description, int id, TaskStatus status, LocalDateTime startTime,
                long duration, int epicId) {
        super(title, description, id, status, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public void setId(int id) {
        if (epicId == id) {
            System.out.println("Нельзя становить id эпика в качестве id");
        } else {
            this.id = id;
        }
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "Подзадача{" +
                "название='" + title + '\'' +
                ", описание='" + description + '\'' +
                ", id='" + id + '\'' +
                ", статус='" + status + '\'' +
                ", id эпика='" + epicId + '}' + '\'' +
                ", дата начала='" + getStartTimeString() + '\'' +
                ", продолжительность='" + duration + '\'' +
                ", дата окончания='" + getEndTimeString() + '\'';
    }
}
