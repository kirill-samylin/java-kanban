package tests;

import app.entities.Epic;
import app.entities.SubTask;
import app.entities.Task;
import app.enums.TaskStatus;
import app.interfaces.TaskManager;
import app.service.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void testCreateAndGetTask() {
        Task task = new Task("Task 1", "Description 1");
        int taskId = taskManager.createNewTask(task);

        Task retrievedTask = taskManager.getTask(taskId);

        assertNotNull(retrievedTask, "Полученная задача не должна быть null");
        assertEquals(task, retrievedTask, "Созданная и полученная задачи должны быть равны");
    }

    @Test
    public void testCreateAndGetSubTask() {
        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = taskManager.createNewEpic(epic);

        SubTask subTask = new SubTask("SubTask 1", "Description 1", epicId);
        int subTaskId = taskManager.createNewSubtask(subTask);

        SubTask retrievedSubTask = taskManager.getSubTask(subTaskId);

        assertNotNull(retrievedSubTask, "Полученная подзадача не должна быть null");
        assertEquals(subTask, retrievedSubTask, "Созданная и полученная подзадачи должны быть равны");
    }

    @Test
    public void testCreateAndGetEpic() {
        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = taskManager.createNewEpic(epic);

        Epic retrievedEpic = taskManager.getEpic(epicId);

        assertNotNull(retrievedEpic, "Полученный эпик не должен быть null");
        assertEquals(epic, retrievedEpic, "Созданный и полученный эпики должны быть равны");
    }

    @Test
    public void testGetTasks() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");
        taskManager.createNewTask(task1);
        taskManager.createNewTask(task2);

        List<Task> tasks = taskManager.getTasks();

        assertEquals(2, tasks.size(), "Должно быть два таска");
        assertTrue(tasks.contains(task1), "Список задач должен содержать task1");
        assertTrue(tasks.contains(task2), "Список задач должен содержать task2");
    }

    @Test
    public void testGetSubTasks() {
        Epic epic = new Epic("Epic", "Description");
        int epicId = taskManager.createNewEpic(epic);

        SubTask subTask1 = new SubTask("SubTask 1", "Description 1", epicId);
        SubTask subTask2 = new SubTask("SubTask 2", "Description 2", epicId);
        taskManager.createNewSubtask(subTask1);
        taskManager.createNewSubtask(subTask2);
        List<SubTask> subTasks = taskManager.getSubTasks();
        assertEquals(2, subTasks.size(), "Должно быть две подзадачи");
        assertTrue(subTasks.contains(subTask1), "Список подзадач должен содержать subTask1");
        assertTrue(subTasks.contains(subTask2), "Список подзадач должен содержать subTask2");
    }

    @Test
    public void testGetEpics() {
        Epic epic1 = new Epic("Epic 1", "Description 1");
        Epic epic2 = new Epic("Epic 2", "Description 2");
        taskManager.createNewEpic(epic1);
        taskManager.createNewEpic(epic2);
        List<Epic> epics = taskManager.getEpics();
        assertEquals(2, epics.size(), "Должно быть два эпика");
        assertTrue(epics.contains(epic1), "Список эпиков должен содержать epic1");
        assertTrue(epics.contains(epic2), "Список эпиков должен содержать epic2");
    }

    @Test
    public void testGetSubTasksByEpic() {
        Epic epic = new Epic("Epic", "Description");
        int epicId = taskManager.createNewEpic(epic);
        SubTask subTask1 = new SubTask("SubTask 1", "Description 1", epicId);
        SubTask subTask2 = new SubTask("SubTask 2", "Description 2", epicId);
        int subTask1Id = taskManager.createNewSubtask(subTask1);
        int subTask2Id = taskManager.createNewSubtask(subTask2);
        epic.addSubTaskId(subTask1Id);
        epic.addSubTaskId(subTask2Id);
        List<SubTask> subTasks = taskManager.getSubTasksByEpic(epicId);
        assertEquals(2, subTasks.size(), "Эпик должен содержать две подзадачи");
        assertTrue(subTasks.contains(subTask1), "Список подзадач должен содержать subTask1");
        assertTrue(subTasks.contains(subTask2), "Список подзадач должен содержать subTask2");
    }

    @Test
    public void testUpdateTask() {
        Task task = new Task("Task", "Description");
        int taskId = taskManager.createNewTask(task);
        Task updatedTask = new Task("Updated Task", "Updated Description");
        updatedTask.setId(taskId);
        updatedTask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(updatedTask);
        Task retrievedTask = taskManager.getTask(taskId);
        assertEquals(updatedTask, retrievedTask, "Задача должна быть обновлена");
    }

    @Test
    public void testUpdateSubTask() {
        Epic epic = new Epic("Epic", "Description");
        int epicId = taskManager.createNewEpic(epic);
        SubTask subTask = new SubTask("SubTask", "Description", epicId);
        int subTaskId = taskManager.createNewSubtask(subTask);
        SubTask updatedSubTask = new SubTask("Updated SubTask", "Updated Description", epicId);
        updatedSubTask.setId(subTaskId);
        updatedSubTask.setStatus(TaskStatus.DONE);
        taskManager.updateSubTask(updatedSubTask);
        SubTask retrievedSubTask = taskManager.getSubTask(subTaskId);
        assertEquals(updatedSubTask, retrievedSubTask, "Подзадача должна быть обновлена");
    }

    @Test
    public void testUpdateEpic() {
        Epic epic = new Epic("Epic", "Description");
        int epicId = taskManager.createNewEpic(epic);
        Epic updatedEpic = new Epic("Updated Epic", "Updated Description");
        updatedEpic.setId(epicId);
        taskManager.updateEpic(updatedEpic);
        Epic retrievedEpic = taskManager.getEpic(epicId);
        assertEquals(updatedEpic, retrievedEpic, "Эпик должен быть обновлен");
    }

    @Test
    public void testRemoveTaskById() {
        Task task = new Task("Task", "Description");
        int taskId = taskManager.createNewTask(task);
        taskManager.removeTaskById(taskId);
        Task retrievedTask = taskManager.getTask(taskId);
        assertNull(retrievedTask, "Задача должна быть удалена из менеджера");
    }

    @Test
    public void testRemoveSubTaskById() {
        Epic epic = new Epic("Epic", "Description");
        int epicId = taskManager.createNewEpic(epic);
        SubTask subTask = new SubTask("SubTask", "Description", epicId);
        int subTaskId = taskManager.createNewSubtask(subTask);
        taskManager.removeSubTaskById(subTaskId);
        SubTask retrievedSubTask = taskManager.getSubTask(subTaskId);
        assertNull(retrievedSubTask, "Подзадача должна быть удалена из менеджера");
        List<SubTask> subTasks = taskManager.getSubTasksByEpic(epicId);
        assertFalse(subTasks.contains(subTask), "Подзадача не должна быть связана с эпиком после удаления");
    }

    @Test
    public void testRemoveEpicById() {
        Epic epic = new Epic("Epic", "Description");
        int epicId = taskManager.createNewEpic(epic);
        SubTask subTask = new SubTask("SubTask", "Description", epicId);
        int subTaskId = taskManager.createNewSubtask(subTask);
        epic.addSubTaskId(subTaskId);
        taskManager.removeEpicById(epicId);
        Epic retrievedEpic = taskManager.getEpic(epicId);
        assertNull(retrievedEpic, "Эпик должен быть удален из менеджера");
        SubTask retrievedSubTask = taskManager.getSubTask(subTask.getId());
        assertNull(retrievedSubTask, "Подзадачи эпика должны быть удалены при удалении эпика");
    }

    @Test
    public void testRemoveAllTasks() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");
        taskManager.createNewTask(task1);
        taskManager.createNewTask(task2);
        taskManager.removeAllTasks();
        List<Task> tasks = taskManager.getTasks();
        assertTrue(tasks.isEmpty(), "Все задачи должны быть удалены");
    }

    @Test
    public void testRemoveAllSubTasks() {
        Epic epic = new Epic("Epic", "Description");
        int epicId = taskManager.createNewEpic(epic);
        SubTask subTask1 = new SubTask("SubTask 1", "Description 1", epicId);
        SubTask subTask2 = new SubTask("SubTask 2", "Description 2", epicId);
        taskManager.createNewSubtask(subTask1);
        taskManager.createNewSubtask(subTask2);
        taskManager.removeAllSubTask();
        List<SubTask> subTasks = taskManager.getSubTasks();
        assertTrue(subTasks.isEmpty(), "Все подзадачи должны быть удалены");
        List<SubTask> epicSubTasks = taskManager.getSubTasksByEpic(epicId);
        assertTrue(epicSubTasks.isEmpty(), "Эпик не должен иметь подзадач после удаления всех подзадач");
    }

    @Test
    public void testRemoveAllEpics() {
        Epic epic1 = new Epic("Epic 1", "Description 1");
        Epic epic2 = new Epic("Epic 2", "Description 2");
        taskManager.createNewEpic(epic1);
        taskManager.createNewEpic(epic2);
        taskManager.removeAllEpic();
        List<Epic> epics = taskManager.getEpics();
        assertTrue(epics.isEmpty(), "Все эпики должны быть удалены");
        List<SubTask> subTasks = taskManager.getSubTasks();
        assertTrue(subTasks.isEmpty(), "Все подзадачи должны быть удалены при удалении всех эпиков");
    }

    @Test
    public void testGetHistory() {
        Task task = new Task("Task", "Description");
        int taskId = taskManager.createNewTask(task);
        // Предполагается, что при получении задачи она добавляется в историю
        taskManager.getTask(taskId);
        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать одну запись");
        assertEquals(task, history.get(0), "История должна содержать полученную задачу");
    }
}