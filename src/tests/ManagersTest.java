package tests;

import app.interfaces.HistoryManager;
import app.interfaces.TaskManager;
import app.service.InMemoryHistoryManager;
import app.service.InMemoryTaskManager;
import app.utils.Managers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ManagersTest {

    @Test
    public void testGetDefault_ReturnsTaskManager() {
        TaskManager taskManager = Managers.getDefault();

        assertNotNull(taskManager, "Метод getDefault() не должен возвращать null");
        assertInstanceOf(InMemoryTaskManager.class, taskManager, "Метод getDefault() должен возвращать экземпляр InMemoryTaskManager");
    }

    @Test
    public void testGetDefault_ReturnsNewInstance() {
        TaskManager firstInstance = Managers.getDefault();
        TaskManager secondInstance = Managers.getDefault();

        assertNotSame(firstInstance, secondInstance, "Метод getDefault() должен возвращать новый экземпляр при каждом вызове");
    }

    @Test
    public void testGetDefaultHistory_ReturnsHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager, "Метод getDefaultHistory() не должен возвращать null");
        assertInstanceOf(InMemoryHistoryManager.class, historyManager, "Метод getDefaultHistory() должен возвращать экземпляр InMemoryHistoryManager");
    }

    @Test
    public void testGetDefaultHistory_ReturnsNewInstance() {
        HistoryManager firstInstance = Managers.getDefaultHistory();
        HistoryManager secondInstance = Managers.getDefaultHistory();

        assertNotSame(firstInstance, secondInstance, "Метод getDefaultHistory() должен возвращать новый экземпляр при каждом вызове");
    }
}