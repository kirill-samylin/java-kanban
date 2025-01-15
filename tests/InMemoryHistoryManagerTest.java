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
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "История должна содержать две задачи после добавления двух задач");
        assertEquals(task1, history.get(0), "Первая задача в истории должна быть task1");
        assertEquals(task2, history.get(1), "Вторая задача в истории должна быть task2");
    }

    @Test
    public void testHistorySizeLimit() {
        for (int i = 1; i <= 10; i++) {
            Task task = new Task("Task " + i, "Description " + i);
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();

        assertEquals(10, history.size(), "История должна содержать 10 задач при размере истории 10");

        // Добавляем ещё одну задачу, которая должна вытеснить самую старую
        Task newTask = new Task("Task 11", "Description 11");
        historyManager.add(newTask);

        history = historyManager.getHistory();

        assertEquals(10, history.size(), "История должна содержать 10 задач после добавления 11-й задачи");
        assertEquals(newTask, history.get(9), "Новая задача должна быть в конце истории");
        assertEquals("Task 2", history.get(0).getTitle(), "Самая старая задача должна быть удалена из истории");
    }

    @Test
    public void testGetHistoryReturnsCopy() {
        Task task = new Task("Task 1", "Description 1");
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        history.add(new Task("Task 2", "Description 2")); // Модифицируем полученную историю
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
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");
        Task task3 = new Task("Task 3", "Description 3");
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        List<Task> history = historyManager.getHistory();
        assertEquals(task1, history.get(0), "Порядок задач должен сохраняться: task1 на позиции 0");
        assertEquals(task2, history.get(1), "Порядок задач должен сохраняться: task2 на позиции 1");
        assertEquals(task3, history.get(2), "Порядок задач должен сохраняться: task3 на позиции 2");
    }

    @Test
    public void testHistoryOverflow() {
        // Добавляем задачи до переполнения истории
        for (int i = 1; i <= 15; i++) {
            Task task = new Task("Task " + i, "Description " + i);
            historyManager.add(task);
        }
        List<Task> history = historyManager.getHistory();
        assertEquals(10, history.size(), "История должна содержать только 10 последних задач после переполнения");
        assertEquals("Task 6", history.get(0).getTitle(), "После переполнения самой старой должна быть задача с названием 'Task 6'");
        assertEquals("Task 15", history.get(9).getTitle(), "Последней задачей должна быть 'Task 15'");
    }

    @Test
    public void testDuplicateTasks() {
        Task task = new Task("Task 1", "Description 1");
        historyManager.add(task);
        historyManager.add(task); // Добавляем ту же задачу дважды
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать две записи после добавления одной и той же задачи дважды");
        assertSame(task, history.get(0), "Первая задача должна быть тем же объектом");
        assertSame(task, history.get(1), "Вторая задача должна быть тем же объектом");
    }
}