package app.interfaces;

import app.entities.Epic;
import app.entities.SubTask;
import app.entities.Task;

import java.util.List;

public interface TaskManager {

    List<Task> getTasks();

    List<SubTask> getSubTasks();

    List<Epic> getEpics();

    Task getTask(int id);

    SubTask getSubTask(int id);

    Epic getEpic(int id);

    List<SubTask> getSubTasksByEpic(int epicId);

    int createNewTask(Task newTask);

    int createNewSubtask(SubTask newSubtask);

    int createNewEpic(Epic newEpic);

    void removeTaskById(int taskId);

    void removeSubTaskById(int subTaskId);

    void removeEpicById(int epicId);

    void removeAllTasks();

    void removeAllSubTask();

    void removeAllEpic();

    void updateTask(Task task);

    void updateSubTask(SubTask subTask);

    void updateEpic(Epic epic);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
