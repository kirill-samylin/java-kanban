package app.entities;

import app.enums.TaskType;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTaskIds;

    public Epic(String title, String description) {
        super(title, description);
        this.subTaskIds = new ArrayList<>();
    }

    public Epic(String title, String description, int id) {
        super(title, description, id);
        this.subTaskIds = new ArrayList<>();
    }

    public Epic(String title, String description, int id, ArrayList<Integer> subTaskIds) {
        super(title, description, id);
        this.subTaskIds = subTaskIds;
    }

    public ArrayList<Integer> getSubTaskIds() {
        return new ArrayList<>(subTaskIds);
    }

    public void setSubTaskIds(ArrayList<Integer> subTaskIds) {
        this.subTaskIds = subTaskIds;
    }

    public void addSubTaskId(int subTaskId) {
        if (subTaskId == id) {
            System.out.println("Нельзя добавить эпик в подзадачи");
        } else {
            subTaskIds.add(subTaskId);
        }
    }

    public void removeSubTaskId(Integer subTaskId) {
        subTaskIds.remove(subTaskId);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return "Эпик{" +
                "название='" + title + '\'' +
                ", описание='" + description + '\'' +
                ", id='" + id + '\'' +
                ", статус='" + status + '\'';
    }
}
