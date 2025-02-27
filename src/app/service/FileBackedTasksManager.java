package app.service;

import app.entities.*;
import app.exceptions.ManagerSaveException;
import app.enums.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final File file;
    private static final String FIRST_LINE = "id,type,name,status,description,epic";

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager tasksManager = new FileBackedTasksManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            List<String> taskLines = reader.lines().toList();
            for (int i = 1; i < taskLines.size(); i++) {
                String[] line = taskLines.get(i).split(",");
                Task task = fromString(line);
                switch (task.getTaskType()) {
                    case TASK:
                        tasksManager.tasks.put(task.getId(), task);
                        break;
                    case EPIC:
                        tasksManager.epics.put(task.getId(), (Epic) task);
                        break;
                    case SUBTASK:
                        tasksManager.subTasks.put(task.getId(), (SubTask) task);
                        int epicId = ((SubTask) task).getEpicId();
                        Epic epic = tasksManager.getEpic(epicId);
                        epic.addSubTaskId(task.getId());
                        tasksManager.refreshEpicStatus(epicId);
                        break;
                }
                if (task.getId() > tasksManager.counter) {
                    tasksManager.counter = task.getId();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tasksManager;
    }

    // конвертация строки в задачу
    private static Task fromString(String[] line) {
        int id = Integer.parseInt(line[0]);
        TaskType taskType = TaskType.valueOf(line[1]);
        String title = line[2];
        String description = line[4];
        switch (taskType) {
            case TASK:
                return new Task(title, description, id);
            case EPIC:
                return new Epic(title, description, id);
            case SUBTASK:
                int epicId = Integer.parseInt(line[5]);
                return new SubTask(title, description, id, epicId);
        }
        return null;
    }

    // метод сохранения
    private void save() throws ManagerSaveException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write(FIRST_LINE);
            writer.newLine();
            addTasksToFile(writer);
            writer.newLine();
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при в записи в файл", e);
        }
    }

    // записываем все задачи в файл
    private void addTasksToFile(BufferedWriter writer) throws IOException {
        for (Task task : getTasks()) {
            writer.write(toString(task));
            writer.newLine();
        }
        for (Epic epic : getEpics()) {
            writer.write(toString(epic));
            writer.newLine();
        }
        for (SubTask subtask : getSubTasks()) {
            writer.write(toString(subtask));
            writer.newLine();
        }
    }

    // конвертирует задачу в строку
    private String toString(Task task) {
        return task.getId() + "," + task.getTaskType() + "," + task.getTitle() + "," + task.getStatus() + "," +
                task.getDescription();
    }

    // конвертирует эпик в строку
    private String toString(Epic epic) {
        return epic.getId() + "," + epic.getTaskType() + "," + epic.getTitle() + "," + epic.getStatus() + "," +
                epic.getDescription();
    }

    // конвертирует подзадачу в строку
    private String toString(SubTask subtask) {
        return subtask.getId() + "," + subtask.getTaskType() + "," + subtask.getTitle() + "," + subtask.getStatus() +
                "," + subtask.getDescription() + "," + subtask.getEpicId();
    }

    @Override
    public int createNewTask(Task newTask) {
        int taskId = super.createNewTask(newTask);
        save();
        return taskId;
    }

    @Override
    public int createNewSubtask(SubTask newSubtask) {
        int subTaskId = super.createNewSubtask(newSubtask);
        save();
        return subTaskId;
    }

    @Override
    public int createNewEpic(Epic newEpic) {
        int epicId = super.createNewEpic(newEpic);
        save();
        return epicId;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubTask() {
        super.removeAllSubTask();
        save();
    }

    @Override
    public void removeAllEpic() {
        super.removeAllEpic();
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int epicId) {
        super.removeEpicById(epicId);
        save();
    }

    @Override
    public void removeSubTaskById(int subtaskIdForRemove) {
        super.removeSubTaskById(subtaskIdForRemove);
        save();
    }

    @Override
    public void updateTask(Task updateTask) {
        super.updateTask(updateTask);
        save();
    }

    @Override
    public void updateEpic(Epic updateEpic) {
        super.updateEpic(updateEpic);
        save();
    }

    @Override
    public void updateSubTask(SubTask updateSubtask) {
        super.updateSubTask(updateSubtask);
        save();
    }
}