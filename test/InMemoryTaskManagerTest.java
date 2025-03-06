import app.entities.*;
import app.enums.TaskStatus;
import app.exceptions.TaskOverlapException;
import app.service.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    private SubTask subTask1;
    private SubTask subTask2;
    private Epic epic;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
        epic = new Epic("Epic", "Description");
        int epicId = taskManager.createNewEpic(epic);
        subTask1 = new SubTask("Subtask 1", "Description 1", epicId);
        subTask2 = new SubTask("Subtask 2", "Description 2", epicId);
        taskManager.createNewSubtask(subTask1);
        taskManager.createNewSubtask(subTask2);
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

    @Test
    public void testHaveStatusDoneWhenAllSubtasksDone() {
        subTask1.setStatus(TaskStatus.DONE);
        subTask2.setStatus(TaskStatus.DONE);
        taskManager.refreshEpicStatus(epic.getId());
        assertEquals(TaskStatus.DONE, epic.getStatus(), "Эпик с подзадачами со статусом DONE должен иметь статус DONE");
    }

    @Test
    public void testHaveStatusNewWhenAllSubtasksNew() {
        assertEquals(TaskStatus.NEW, epic.getStatus(), "Эпик с подзадачами со статусом NEW должен иметь статус NEW");
    }

    @Test
    public void testHaveStatusInProgressWhenAllSubtasksInProgress() {
        subTask1.setStatus(TaskStatus.IN_PROGRESS);
        subTask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.refreshEpicStatus(epic.getId());
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Эпик с подзадачами со статусом IN_PROGRESS должен иметь статус IN_PROGRESS");
    }

    @Test
    public void testHaveStatusInNewSubtasksDoneAndNew() {
        subTask1.setStatus(TaskStatus.DONE);
        taskManager.refreshEpicStatus(epic.getId());
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Эпик с подзадачами со статусом DONE и NEW должен иметь статус IN_PROGRESS");
    }

    @Test
    public void testAddTaskWhenNoOverlap() {
        LocalDateTime startTime = LocalDateTime.of(2023, 10, 1, 12, 0);
        Task task1 = new Task("New Task", "Description", startTime, 60 * 5);
        Task task2 = new Task("New Task", "Description", startTime.plusDays(2), 60 * 5);
        taskManager.createNewTask(task1);
        assertDoesNotThrow(() -> taskManager.createNewTask(task2), "Задача не должна пересекаться и не должна вызывать исключение");
    }

    @Test
    public void testThrowExceptionWhenTasksOverlap() {
        LocalDateTime startTime = LocalDateTime.of(2023, 10, 1, 12, 0);
        Task task1 = new Task("New Task", "Description", startTime, 60 * 5);
        Task task2 = new Task("New Task", "Description", startTime.plusMinutes(60), 60 * 5);
        taskManager.createNewTask(task1);

        TaskOverlapException exception = assertThrows(TaskOverlapException.class, () -> {
            taskManager.createNewTask(task2);
        }, "Должно быть выброшено исключение при пересечении задач");

        assertEquals("Время задачи пересекается с временем текущих задач!", exception.getMessage());
    }

}