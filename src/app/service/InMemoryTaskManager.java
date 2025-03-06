package app.service;

import app.entities.Epic;
import app.entities.SubTask;
import app.entities.Task;
import app.enums.TaskStatus;
import app.exceptions.TaskOverlapException;
import app.interfaces.HistoryManager;
import app.interfaces.TaskManager;
import app.utils.Managers;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int counter = 1;
    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, SubTask> subTasks;
    protected final HashMap<Integer, Epic> epics;
    private final HistoryManager historyManager;

    final static Comparator<Task> COMPARATOR = Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId);

    protected Set<Task> prioritizedTasks = new TreeSet<>(COMPARATOR);

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
        return epics.get(epicId).getSubTaskIds().stream().map(subTasks::get).toList();
    }

    @Override
    public int createNewTask(Task newTask) {
        validate(newTask);
        int id = generateId();
        newTask.setId(id);
        tasks.put(id, newTask);
        prioritizedTasks.add(newTask);
        return id;
    }

    @Override
    public int createNewSubtask(SubTask newSubtask) {
        validate(newSubtask);
        int id = generateId();
        newSubtask.setId(id);
        int epicId = newSubtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic != null) epic.addSubTaskId(id);
        subTasks.put(id, newSubtask);
        refreshEpicStatus(epicId);
        refreshEpicTime(epicId);
        prioritizedTasks.add(newSubtask);
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
            Task removedTask = tasks.remove(taskId);
            prioritizedTasks.remove(removedTask);
        } else {
            System.out.println("Не найдена задача по индентификатору: " + taskId);
        }
    }

    @Override
    public void removeSubTaskById(int subTaskId) {
        SubTask subTask = subTasks.remove(subTaskId);
        prioritizedTasks.remove(subTask);
        if (subTask != null) {
            int epicId = subTask.getEpicId();
            if (epics.containsKey(epicId)) {
                Epic epic = epics.get(epicId);
                epic.removeSubTaskId(subTaskId);
                refreshEpicStatus(epicId);
                refreshEpicTime(epicId);
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
                SubTask removedSubTask = subTasks.remove(subTaskId);
                prioritizedTasks.remove(removedSubTask);
            }
            epics.remove(epicId);
            System.out.println("Эпик " + epicToRemove.getTitle() + " удален");
        } else {
            System.out.println("Не найден эпик по индентификатору: " + epicId);
        }
    }

    @Override
    public void removeAllTasks() {
        tasks.forEach((taskId, task) -> {
            historyManager.remove(taskId);
            prioritizedTasks.remove(task);
        });
        tasks.clear();
    }

    @Override
    public void removeAllSubTask() {
        subTasks.forEach((taskId, task) -> {
            historyManager.remove(taskId);
            prioritizedTasks.remove(task);
        });
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.setSubTaskIds(new ArrayList<>());
            int epicId = epic.getId();
            refreshEpicStatus(epicId);
            refreshEpicTime(epicId);
        }
    }

    @Override
    public void removeAllEpic() {
        epics.clear();
        removeAllSubTask();
    }

    public void refreshEpicStatus(int epicId) {
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
        validate(task);
        int taskId = task.getId();
        if (tasks.containsKey(taskId)) {
            tasks.put(taskId, task);
        } else {
            System.out.println("Перед обновлением необходимо добавить задачу");
        }

    }

    @Override
    public void updateSubTask(SubTask subTask) {
        validate(subTask);
        int subTaskId = subTask.getId();
        if (subTasks.containsKey(subTaskId)) {
            subTasks.put(subTaskId, subTask);
            int epicId = subTask.getEpicId();
            refreshEpicStatus(epicId);
            refreshEpicTime(epicId);
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


    @Override
    public void refreshEpicTime(int epicId) {
        Epic epicToUpdate = epics.get(epicId);
        if (epicToUpdate == null) return;

        List<Integer> subTaskIds = epicToUpdate.getSubTaskIds();
        LocalDateTime epicStartTime = null;
        LocalDateTime epicEndTime = null;
        long epicDuration = 0L;

        for (Integer subTaskId : subTaskIds) {
            SubTask subTask = subTasks.get(subTaskId);
            LocalDateTime subTaskStartTime = subTask.getStartTime();
            LocalDateTime subTaskEndTime = subTask.getEndTime();
            if (subTaskStartTime != null && (epicStartTime == null || subTaskStartTime.isBefore(epicStartTime))) {
                epicStartTime = subTaskStartTime;
            }
            if (subTaskEndTime != null && (epicEndTime == null || subTaskEndTime.isAfter(epicEndTime))) {
                epicEndTime = subTaskEndTime;
            }
            epicDuration += subTask.getDuration();
        }

        epicToUpdate.setStartTime(epicStartTime);
        epicToUpdate.setEndTime(epicEndTime);
        epicToUpdate.setDuration(epicDuration);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public void validate(Task taskToValidate) throws TaskOverlapException {
        LocalDateTime newStartTime = taskToValidate.getStartTime();
        LocalDateTime newEndTime = taskToValidate.getEndTime();
        if (newStartTime == null) return;

        List<Task> prioritizedTasks = getPrioritizedTasks();

        boolean hasOverlap = prioritizedTasks.stream()
                .filter(task -> task.getStartTime() != null && task.getEndTime() != null)
                .anyMatch(task ->
                        newStartTime.isBefore(task.getEndTime()) && task.getStartTime().isBefore(newEndTime)
                );

        if (hasOverlap) {
            throw new TaskOverlapException("Время задачи пересекается с временем текущих задач!");
        }
    }
}
