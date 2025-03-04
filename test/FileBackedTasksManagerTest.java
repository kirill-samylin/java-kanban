import app.entities.*;
import app.service.FileBackedTasksManager;
import org.junit.jupiter.api.*;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTasksManagerTest {
    private File file;
    private FileBackedTasksManager tasksManager;

    @BeforeEach
    void setUp() {
        file = new File("resources/test.csv");
        tasksManager = new FileBackedTasksManager(file);
    }

    @Test
    void testFileManager() {
        Task task = new Task("Task 1", "Description 1", 1);
        Epic epic = new Epic("Epic 2", "Description 2", 2);
        SubTask subTask = new SubTask("SubTask 2", "Description 3", 3, epic.getId());
        tasksManager.createNewTask(task);
        tasksManager.createNewEpic(epic);
        tasksManager.createNewSubtask(subTask);

        FileBackedTasksManager fileManager = FileBackedTasksManager.loadFromFile(file);
        assertEquals(tasksManager.getTasks(), fileManager.getTasks(), "Задачи после выгрузки совпадает");
        assertEquals(tasksManager.getSubTasks(), fileManager.getSubTasks(), "Подзадачи после выгрузки совпадает");
        assertEquals(tasksManager.getEpics(), fileManager.getEpics(), "Эпики после выгрузки совпадает");
    }

    @Test
    void testLoadEmptyFile() {
        FileBackedTasksManager fileManager = FileBackedTasksManager.loadFromFile(new File("resources/empty.csv"));
        assertEquals(fileManager.getTasks().size(), 0, "Задачи отсутсвуют");
        assertEquals(fileManager.getSubTasks().size(), 0, "Подзадачи отсутсвуют");
        assertEquals(fileManager.getEpics().size(), 0, "Эпики отсутсвуют");
    }
}