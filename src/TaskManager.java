import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int counter = 0;
    public HashMap<Integer, Task> tasks;
    public HashMap<Integer, SubTask> subTasks;
    public HashMap<Integer, Epic> epics;

    public TaskManager() {
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epics = new HashMap<>();
    }

    private int generateId() {
        counter++;
        return counter - 1;
    };

    public Iterable<Task> getTasks() {
        return tasks.values();
    }

    public Iterable<SubTask> getSubTasks() {
        return subTasks.values();
    }

    public Iterable<Epic> getEpics() {
        return epics.values();
    }

    public Iterable<SubTask> getSubTasksByEpic(int epicId) {
        ArrayList<SubTask> epicSubTasks = new ArrayList<>();
        for (SubTask subTask : subTasks.values()) {
            if (subTask.getEpicId() == epicId) {
                epicSubTasks.add(subTask);
            }
        }
        return epicSubTasks;
    }

    public void createNewTask(Task newTask) {
        int id = generateId();
        newTask.setId(id);
        newTask.setStatus(TaskStatus.NEW);
        tasks.put(id, newTask);
    }

    public void createNewSubtask(SubTask newSubtask) {
        int id = generateId();
        newSubtask.setId(id);
        newSubtask.setStatus(TaskStatus.NEW);
        subTasks.put(id, newSubtask);
    }

    public void createNewEpic(Epic newEpic) {
        int id = generateId();
        newEpic.setId(id);
        newEpic.setStatus(TaskStatus.NEW);
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
        if (subTasks.containsKey(subTaskId)) {
            SubTask subTask = subTasks.get(subTaskId);
            subTasks.remove(subTaskId);
            if (epics.containsKey(subTask.getEpicId())) {
                Epic epic = epics.get(subTask.getEpicId());
                epic.removeSubTaskId(subTaskId);
                refreshEpicStatus(epic.getId());
            }
            System.out.println("Подзадача " + subTask.getTitle() + " удалена");
        } else {
            System.out.println("Не найдена подзадача по индентификатору: " + subTaskId);
        }
    }

    public void removeEpicById(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epicToRemove = epics.get(epicId);
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
    }

    public void removeAllEpic() {
        epics.clear();
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    private void refreshEpicStatus(int epicId) {
        if (epics.containsKey(epicId)) {
            TaskStatus newEpicStatus = TaskStatus.NEW;
            Epic epic = epics.get(epicId);
            ArrayList<Integer> epicSubTaskIds = epic.getSubTaskIds();
            for (Integer subTaskId : epicSubTaskIds) {
                if (subTasks.containsKey(subTaskId)) {
                    SubTask subTask = subTasks.get(subTaskId);
                    TaskStatus subTaskStatus = subTask.getStatus();
                    if (subTaskStatus == TaskStatus.IN_PROGRESS || (subTaskStatus == TaskStatus.NEW && newEpicStatus != TaskStatus.NEW)) {
                        newEpicStatus = TaskStatus.IN_PROGRESS;
                        break;
                    }
                    if (subTaskStatus == TaskStatus.DONE) {
                        newEpicStatus = TaskStatus.DONE;
                    }
                }
            }
            epic.setStatus(newEpicStatus);
            updateEpic(epic);
        }
    }

    public void updateSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        refreshEpicStatus(subTask.getEpicId());
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void printEpics() {
        System.out.println("Список всех эпиков: ");
        for (Task epic : epics.values()) {
            System.out.println(epic.toString());
        }
    }

    public void printTasks() {
        System.out.println("Список всех задач: ");
        for (Task task : tasks.values()) {
            System.out.println(task.toString());
        }
    }

    public void printSubTasks() {
        System.out.println("Список всех подзадач: ");
        for (Task subTask : subTasks.values()) {
            System.out.println(subTask.toString());
        }
    }
}
