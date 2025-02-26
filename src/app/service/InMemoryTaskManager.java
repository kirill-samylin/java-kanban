package app.service;

import app.entities.Epic;
import app.entities.SubTask;
import app.entities.Task;
import app.enums.TaskStatus;
import app.interfaces.HistoryManager;
import app.interfaces.TaskManager;
import app.utils.Managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int counter = 1;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, SubTask> subTasks;
    private final HashMap<Integer, Epic> epics;
    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    private int generateId() {
        return counter++;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public List<SubTask> getSubTasksByEpic(int epicId) {
        ArrayList<SubTask> epicSubTasks = new ArrayList<>();
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            ArrayList<Integer> epicSubTaskIds = epic.getSubTaskIds();
            for (int subTaskId : epicSubTaskIds) {
                if (subTasks.containsKey(subTaskId)) {
                    epicSubTasks.add(subTasks.get(subTaskId));
                }
            }
        } else {
            System.out.println("Не найден эпик по индентификатору: " + epicId);
        }
        return epicSubTasks;
    }

    @Override
    public int createNewTask(Task newTask) {
        int id = generateId();
        newTask.setId(id);
        tasks.put(id, newTask);
        return id;
    }

    @Override
    public int createNewSubtask(SubTask newSubtask) {
        int id = generateId();
        newSubtask.setId(id);
        refreshEpicStatus(newSubtask.getEpicId());
        subTasks.put(id, newSubtask);
        return id;
    }

    @Override
    public int createNewEpic(Epic newEpic) {
        int id = generateId();
        newEpic.setId(id);
        epics.put(id, newEpic);
        return id;
    }

    @Override
    public void removeTaskById(int taskId) {
        if (tasks.containsKey(taskId)) {
            tasks.remove(taskId);
        } else {
            System.out.println("Не найдена задача по индентификатору: " + taskId);
        }
    }

    @Override
    public void removeSubTaskById(int subTaskId) {
        SubTask subTask = subTasks.remove(subTaskId);
        if (subTask != null) {
            int epicId = subTask.getEpicId();
            if (epics.containsKey(epicId)) {
                Epic epic = epics.get(epicId);
                epic.removeSubTaskId(subTaskId);
                refreshEpicStatus(epic.getId());
            }
            System.out.println("Подзадача " + subTask.getTitle() + " удалена");
        } else {
            System.out.println("Не найдена подзадача по индентификатору: " + subTaskId);
        }
    }

    @Override
    public void removeEpicById(int epicId) {
        Epic epicToRemove = epics.remove(epicId);
        if (epicToRemove != null) {
            ArrayList<Integer> subTaskIdsToRemove = epicToRemove.getSubTaskIds();
            for (Integer subTaskId : subTaskIdsToRemove) {
                subTasks.remove(subTaskId);
            }
            epics.remove(epicId);
            System.out.println("Эпик " + epicToRemove.getTitle() + " удален");
        } else {
            System.out.println("Не найден эпик по индентификатору: " + epicId);
        }
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeAllSubTask() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.setSubTaskIds(new ArrayList<>());
            refreshEpicStatus(epic.getId());
        }
    }

    @Override
    public void removeAllEpic() {
        epics.clear();
        subTasks.clear();
    }

    private void refreshEpicStatus(int epicId) {
        if (epics.containsKey(epicId)) {
            TaskStatus newEpicStatus = TaskStatus.NEW;
            Epic epic = epics.get(epicId);

            ArrayList<Integer> epicSubTaskIds = epic.getSubTaskIds();
            int countEpicSubTasks = epicSubTaskIds.size();
            int countNewSubTasks = 0;
            int countDoneSubTasks = 0;
            for (Integer subTaskId : epicSubTaskIds) {
                SubTask subTask = subTasks.get(subTaskId);
                TaskStatus subTaskStatus = subTask.getStatus();
                if (subTaskStatus == TaskStatus.NEW) {
                    countNewSubTasks++;
                }
                if (subTaskStatus == TaskStatus.DONE) {
                    countDoneSubTasks++;
                }
            }
            if (countEpicSubTasks > 0) {
                if (countDoneSubTasks == countEpicSubTasks) {
                    newEpicStatus = TaskStatus.DONE;
                } else if (countNewSubTasks != countEpicSubTasks) {
                    newEpicStatus = TaskStatus.IN_PROGRESS;
                }
            }

            epic.setStatus(newEpicStatus);
        }
    }

    @Override
    public void updateTask(Task task) {
        int taskId = task.getId();
        if (tasks.containsKey(taskId)) {
            tasks.put(taskId, task);
        } else {
            System.out.println("Перед обновлением необходимо добавить задачу");
        }

    }

    @Override
    public void updateSubTask(SubTask subTask) {
        int subTaskId = subTask.getId();
        if (subTasks.containsKey(subTaskId)) {
            subTasks.put(subTaskId, subTask);
            refreshEpicStatus(subTask.getEpicId());
        } else {
            System.out.println("Перед обновлением необходимо добавить подзадачу");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        int epicId = epic.getId();
        if (epics.containsKey(epicId)) {
            epics.put(epicId, epic);
        } else {
            System.out.println("Перед обновлением необходимо добавить эпик");
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
