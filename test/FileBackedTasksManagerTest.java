import app.entities.*;
import app.service.FileBackedTasksManager;
import org.junit.jupiter.api.*;

import java.io.File;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    private File file;

    @BeforeEach
    void setUp() {
        file = new File("resources/test.csv");
        taskManager = new FileBackedTasksManager(file);
        taskManager.removeAllTasks();
        taskManager.removeAllEpic();
        taskManager.removeAllSubTask();
    }

    @Test
    void testFileManager() {
        Task task = new Task("Task 1", "Description 1", 1);
        Epic epic = new Epic("Epic 2", "Description 2", 2);
        SubTask subTask = new SubTask("SubTask 2", "Description 3", 3, epic.getId());
        taskManager.createNewTask(task);
        taskManager.createNewEpic(epic);
        taskManager.createNewSubtask(subTask);

        FileBackedTasksManager fileManager = FileBackedTasksManager.loadFromFile(file);
        assertEquals(taskManager.getTasks(), fileManager.getTasks(), "Задачи после выгрузки совпадает");
        assertEquals(taskManager.getSubTasks(), fileManager.getSubTasks(), "Подзадачи после выгрузки совпадает");
        assertEquals(taskManager.getEpics(), fileManager.getEpics(), "Эпики после выгрузки совпадает");
    }

    @Test
    void testLoadEmptyFile() {
        FileBackedTasksManager fileManager = FileBackedTasksManager.loadFromFile(new File("resources/empty.csv"));
        assertEquals(0, fileManager.getTasks().size(), "Задачи отсутсвуют");
        assertEquals(0, fileManager.getSubTasks().size(), "Подзадачи отсутсвуют");
        assertEquals(0, fileManager.getEpics().size(), "Эпики отсутсвуют");
    }

    @Test
    void testLoadPrioritizedTasks() {
        LocalDateTime startTime = LocalDateTime.of(2020, 10, 1, 12, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2025,5, 1, 12, 0);
        Task task1 = new Task("New Task", "Description", startTime, DURATION);
        Task task2 = new Task("New Task", "Description", startTime2, DURATION);
        System.out.println(taskManager.getPrioritizedTasks());
        System.out.println(taskManager.getTasks());
        taskManager.createNewTask(task2);
        taskManager.createNewTask(task1);
        FileBackedTasksManager fileManager = FileBackedTasksManager.loadFromFile(file);

        assertEquals(task1, fileManager.getPrioritizedTasks().getFirst(), "Созданная и полученная задачи должны быть равны");
    }
}