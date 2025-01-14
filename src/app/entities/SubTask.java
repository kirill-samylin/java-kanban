package app.entities;

import app.enums.TaskType;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    };

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
                ", id эпика='" + epicId + '}' + '\'';
    }
}
