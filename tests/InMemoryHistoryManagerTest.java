import app.entities.Task;
import app.interfaces.HistoryManager;
import app.service.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    public void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    public void testAddAndGetHistory() {
        Task task1 = new Task("Task 1", "Description 1", 1);
        Task task2 = new Task("Task 2", "Description 2", 2);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "История должна содержать две задачи после добавления двух задач");
        assertEquals(task1, history.get(0), "Первая задача в истории должна быть task1");
        assertEquals(task2, history.get(1), "Вторая задача в истории должна быть task2");
    }

    @Test
    public void testRemoveTaskFromHistory() {
        // Создаем экземпляр InMemoryHistoryManager
        HistoryManager historyManager = new InMemoryHistoryManager();

        // Создаем задачи
        Task task1 = new Task("Task 1", "Description 1", 1);
        Task task2 = new Task("Task 2", "Description 2", 2);
        Task task3 = new Task("Task 3", "Description 3", 3);

        // Добавляем задачи в историю
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        // Удаляем задачу 2
        historyManager.remove(2);

        // Получаем историю и проверяем
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать 2 задачи после удаления");
        assertEquals(task1, history.get(0), "Первая задача должна быть task1");
        assertEquals(task3, history.get(1), "Вторая задача должна быть task3");
    }

    @Test
    public void testGetHistoryReturnsCopy() {
        Task task = new Task("Task 1", "Description 1", 1);
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        history.add(new Task("Task 2", "Description 2", 1)); // Модифицируем полученную историю
        List<Task> internalHistory = historyManager.getHistory();
        assertEquals(1, internalHistory.size(), "Изменения в возвращённой истории не должны влиять на внутреннее состояние");
    }

    @Test
    public void testAddNullTask() {
        historyManager.add(null);
        List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size(), "История не должна содержать задачи после попытки добавить null");
    }

    @Test
    public void testOrderPreservation() {
        Task task1 = new Task("Task 1", "Description 1", 1);
        Task task2 = new Task("Task 2", "Description 2", 2);
        Task task3 = new Task("Task 3", "Description 3", 3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        List<Task> history = historyManager.getHistory();
        assertEquals(task1, history.get(0), "Порядок задач должен сохраняться: task1 на позиции 0");
        assertEquals(task2, history.get(1), "Порядок задач должен сохраняться: task2 на позиции 1");
        assertEquals(task3, history.get(2), "Порядок задач должен сохраняться: task3 на позиции 2");
    }

    @Test
    public void testDuplicateTasks() {
        // Создаем экземпляр InMemoryHistoryManager
        HistoryManager historyManager = new InMemoryHistoryManager();

        // Создаем задачи
        Task task1 = new Task("Task 1", "Description 1", 1);
        Task task2 = new Task("Task 2", "Description 2", 2);
        Task task3 = new Task("Task 3", "Description 3", 3);

        // Добавляем задачи в историю
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        // Повторно добавляем task1
        historyManager.add(task1);

        // Получаем историю и проверяем порядок
        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size(), "История должна содержать 3 задачи");
        assertEquals(task2, history.get(0), "Первая задача должна быть task2");
        assertEquals(task3, history.get(1), "Вторая задача должна быть task3");
        assertEquals(task1, history.get(2), "Третья задача должна быть task1 (перемещена в конец)");
    }
}