package app.service;

import app.entities.*;
import app.exceptions.ManagerSaveException;
import app.enums.*;
import app.utils.LocalDateAdapter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final File file;
    private static final String FIRST_LINE = "id,type,name,status,description,start_date,duration,end_date,epic";

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
                switch (Objects.requireNonNull(task).getTaskType()) {
                    case TASK:
                        tasksManager.tasks.put(task.getId(), task);
                        prioritizedTasks.add(task);
                        break;
                    case EPIC:
                        tasksManager.epics.put(task.getId(), (Epic) task);
                        break;
                    case SUBTASK:
                        tasksManager.subTasks.put(task.getId(), (SubTask) task);
                        prioritizedTasks.add(task);
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
            throw new ManagerSaveException("Ошибка при в чтении из файла", e);
        }
        return tasksManager;
    }

    // конвертация строки в задачу
    private static Task fromString(String[] line) {
        if (line.length == 0) return null;
        int id = Integer.parseInt(line[0]);
        TaskType taskType = TaskType.valueOf(line[1]);
        String title = line[2];
        TaskStatus status = TaskStatus.valueOf(line[3]);
        String description = line[4];
        LocalDateTime startTime;
        if (!line[5].equals("null")) {
            startTime = LocalDateTime.parse(line[5], LocalDateAdapter.formatter);
        } else {
            startTime = null;
        }
        Duration duration = Duration.ofMinutes(line[6].equals("null") ? 0 : Long.parseLong(line[6]));

        return switch (taskType) {
            case TASK -> new Task(title, description, id, status, startTime, duration);
            case EPIC -> new Epic(title, description, id);
            case SUBTASK -> {
                int epicId = Integer.parseInt(line[8]);
                yield new SubTask(title, description, id, status, startTime, duration, epicId);
            }
        };
    }

    // метод сохранения
    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write(FIRST_LINE);
            addTasksToFile(writer);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при в записи в файл", e);
        }
    }

    // записываем все задачи в файл
    private void addTasksToFile(BufferedWriter writer) throws IOException {
        for (Task task : getTasks()) {
            writer.newLine();
            writer.append(toStringTask(task));
        }
        for (Epic epic : getEpics()) {
            writer.newLine();
            writer.write(toStringEpic(epic));
        }
        for (SubTask subtask : getSubTasks()) {
            writer.newLine();
            writer.write(toStringSubTask(subtask));
        }
    }

    // конвертирует задачу в строку
    private String toStringTask(Task task) {
        return task.getId() + "," + task.getTaskType() + "," + task.getTitle() + "," + task.getStatus() + "," +
                task.getDescription() + "," + task.getStartTimeString() + "," + task.getDuration().toMinutes() + "," +
                task.getEndTimeString();
    }

    // конвертирует эпик в строку
    private String toStringEpic(Epic epic) {
        return toStringTask(epic);
    }

    // конвертирует подзадачу в строку
    private String toStringSubTask(SubTask subtask) {
        return toStringTask(subtask) + "," + subtask.getEpicId();
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