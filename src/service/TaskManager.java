package service;

import entities.Epic;
import entities.SubTask;
import entities.Task;
import enums.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private int counter = 1;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, SubTask> subTasks;
    private HashMap<Integer, Epic> epics;

    public TaskManager() {
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
    }

    private int generateId() {
        return counter++;
    };

    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public SubTask getSubTask(int id) {
        return subTasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

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

    public void createNewTask(Task newTask) {
        int id = generateId();
        newTask.setId(id);
        tasks.put(id, newTask);
    }

    public void createNewSubtask(SubTask newSubtask) {
        int id = generateId();
        newSubtask.setId(id);
        refreshEpicStatus(newSubtask.getEpicId());
        subTasks.put(id, newSubtask);
    }

    public void createNewEpic(Epic newEpic) {
        int id = generateId();
        newEpic.setId(id);
        epics.put(id, newEpic);
    }

    public void removeTaskById(int taskId) {
        if (tasks.containsKey(taskId)) {
            tasks.remove(taskId);
        } else {
            System.out.println("Не найдена задача по индентификатору: " + taskId);
        }
    }

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

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllSubTask() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.setSubTaskIds(new ArrayList<>());
            refreshEpicStatus(epic.getId());
        }
    }

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

    public void updateTask(Task task) {
        int taskId = task.getId();
        if (tasks.containsKey(taskId)) {
            tasks.put(taskId, task);
        } else {
            System.out.println("Перед обновлением необходимо добавить задачу");
        }

    }

    public void updateSubTask(SubTask subTask) {
        int subTaskId = subTask.getId();
        if (subTasks.containsKey(subTaskId)) {
            subTasks.put(subTaskId, subTask);
            refreshEpicStatus(subTask.getEpicId());
        } else {
            System.out.println("Перед обновлением необходимо добавить подзадачу");
        }
    }

    public void updateEpic(Epic epic) {
        int epicId = epic.getId();
        if (epics.containsKey(epicId)) {
            epics.put(epicId, epic);
        } else {
            System.out.println("Перед обновлением необходимо добавить эпик");
        }
    }
}
