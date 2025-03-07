import app.entities.Task;
import app.exceptions.TaskOverlapException;
import app.service.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void testAddTaskWhenNoOverlap() {
        LocalDateTime startTime = LocalDateTime.of(2025, 10, 1, 12, 0);
        Task task1 = new Task("New Task", "Description", startTime, DURATION);
        Task task2 = new Task("New Task", "Description", startTime.plusDays(2), DURATION);
        taskManager.createNewTask(task1);
        assertDoesNotThrow(() -> taskManager.createNewTask(task2), "Задача не должна пересекаться и не должна вызывать исключение");
    }

    @Test
    public void testThrowExceptionWhenTasksOverlap() {
        LocalDateTime startTime = LocalDateTime.of(2023, 10, 1, 12, 0);
        Task task1 = new Task("New Task", "Description", startTime, DURATION);
        Task task2 = new Task("New Task", "Description", startTime.plusMinutes(60), DURATION);
        taskManager.createNewTask(task1);
        TaskOverlapException exception = assertThrows(TaskOverlapException.class, () -> {
            taskManager.createNewTask(task2);
        }, "Должно быть выброшено исключение при пересечении задач");

        assertEquals("Время задачи пересекается с временем текущих задач!", exception.getMessage());
    }
}