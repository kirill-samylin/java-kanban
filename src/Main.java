import app.entities.Epic;
import app.entities.SubTask;
import app.entities.Task;
import app.enums.TaskStatus;
import app.interfaces.TaskManager;
import app.utils.Managers;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Первая задача", "описания первой задачи");
        taskManager.createNewTask(task1);
        Task task2 = new Task("Вторая задача", "описания второй задачи");
        taskManager.createNewTask(task2);

        Epic epic1 = new Epic("Эпик1", "эпик с двумя подзадачами");
        taskManager.createNewEpic(epic1);
        int epic1Id = epic1.getId();
        SubTask subTask1 = new SubTask("Подзадача 1", "1", epic1Id);
        taskManager.createNewSubtask(subTask1);
        SubTask subTask2 = new SubTask("Подзадача 2", "2", epic1Id);
        taskManager.createNewSubtask(subTask2);

        epic1.addSubTaskId(subTask1.getId());
        epic1.addSubTaskId(subTask2.getId());
        taskManager.updateEpic(epic1);

        Epic epic2 = new Epic("Эпик2", "эпик с одной подзадачей");
        taskManager.createNewEpic(epic2);
        int epic2Id = epic2.getId();
        SubTask subTask3 = new SubTask("Подзадача 3", "3", epic2Id);
        taskManager.createNewSubtask(subTask3);
        epic2.addSubTaskId(subTask3.getId());
        taskManager.updateEpic(epic2);
        System.out.println();
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getSubTasks());

        subTask1.setStatus(TaskStatus.DONE);
        taskManager.updateSubTask(subTask1);
        subTask2.setStatus(TaskStatus.DONE);
        taskManager.updateSubTask(subTask2);
        subTask3.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubTask(subTask3);
        System.out.println();
        System.out.println(taskManager.getEpics());

        taskManager.removeTaskById(task1.getId());
        taskManager.removeEpicById(epic1.getId());
        System.out.println();

        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getSubTasks());

    }
}
