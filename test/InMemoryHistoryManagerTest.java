import app.entities.Task;
import app.interfaces.HistoryManager;
import app.utils.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;
    private Task task4;
    private Task task5;

    @BeforeEach
    public void setUp() {
        historyManager = Managers.getDefaultHistory();
        task1 = new Task("Task 1", "Description 1", 1);
        task2 = new Task("Task 2", "Description 2", 2);
        task3 = new Task("Task 3", "Description 3", 3);
        task4 = new Task("Task 4", "Description 4", 4);
        task5 = new Task("Task 5", "Description 5", 5);

        // Добавляем задачи в историю
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);
        historyManager.add(task5);
    }

    @Test
    public void testAddAndGetHistory() {
        List<Task> history = historyManager.getHistory();
        assertEquals(5, history.size(), "История должна содержать пять задач после добавления пяти задач");
        assertEquals(task1, history.get(0), "Первая задача в истории должна быть task1");
        assertEquals(task2, history.get(1), "Вторая задача в истории должна быть task2");
    }

    @Test
    public void testRemoveTaskFromHistory() {
        // Удаляем задачу 2
        historyManager.remove(2);

        // Получаем историю и проверяем
        List<Task> history = historyManager.getHistory();
        assertEquals(4, history.size(), "История должна содержать 2 задачи после удаления");
        assertEquals(task1, history.get(0), "Первая задача должна быть task1");
        assertEquals(task3, history.get(1), "Вторая задача должна быть task3");
    }

    @Test
    public void testGetHistoryReturnsCopy() {
        List<Task> history = historyManager.getHistory();
        history.add(new Task("Task 2", "Description 2", 1)); // Модифицируем полученную историю
        List<Task> internalHistory = historyManager.getHistory();
        assertEquals(5, internalHistory.size(), "Изменения в возвращённой истории не должны влиять на внутреннее состояние");
    }

    @Test
    public void testAddNullTask() {
        historyManager.add(null);
        List<Task> history = historyManager.getHistory();
        assertEquals(5, history.size(), "История не должна содержать задачи после попытки добавить null");
    }

    @Test
    public void testOrderPreservation() {
        List<Task> history = historyManager.getHistory();
        assertEquals(task1, history.get(0), "Порядок задач должен сохраняться: task1 на позиции 0");
        assertEquals(task2, history.get(1), "Порядок задач должен сохраняться: task2 на позиции 1");
        assertEquals(task3, history.get(2), "Порядок задач должен сохраняться: task3 на позиции 2");
    }

    @Test
    public void testDuplicateTasks() {
        // Повторно добавляем task1
        historyManager.add(task1);

        // Получаем историю и проверяем порядок
        List<Task> history = historyManager.getHistory();
        assertEquals(5, history.size(), "История должна содержать 3 задачи");
        assertEquals(task2, history.get(0), "Первая задача должна быть task2");
        assertEquals(task3, history.get(1), "Вторая задача должна быть task3");
        assertEquals(task1, history.get(4), "Пятая задача должна быть task1 (перемещена в конец)");
    }

    @Test
    public void testRemoveFromMiddle() {
        // Удаляем задачу из середины истории
        historyManager.remove(task3.getId());

        // Получаем обновлённую историю
        List<Task> history = historyManager.getHistory();

        // Проверяем, что размер истории уменьшился
        assertEquals(4, history.size(), "Размер истории должен быть 4 после удаления из середины");

        // Проверяем порядок оставшихся задач
        assertEquals(task1, history.get(0), "Первая задача должна быть task1");
        assertEquals(task2, history.get(1), "Вторая задача должна быть task2");
        assertEquals(task4, history.get(2), "Третья задача должна быть task4");
        assertEquals(task5, history.get(3), "Четвёртая задача должна быть task5");
    }

    @Test
    public void testRemoveFromBeginning() {
        // Удаляем задачу из начала истории
        historyManager.remove(task1.getId());

        // Получаем обновлённую историю
        List<Task> history = historyManager.getHistory();

        // Проверяем, что размер истории уменьшился
        assertEquals(4, history.size(), "Размер истории должен быть 4 после удаления из начала");

        // Проверяем порядок оставшихся задач
        assertEquals(task2, history.get(0), "Первая задача должна быть task2");
        assertEquals(task3, history.get(1), "Вторая задача должна быть task3");
        assertEquals(task4, history.get(2), "Третья задача должна быть task4");
        assertEquals(task5, history.get(3), "Четвёртая задача должна быть task5");
    }

    @Test
    public void testRemoveFromEnd() {
        // Удаляем задачу из конца истории
        historyManager.remove(task5.getId());

        // Получаем обновлённую историю
        List<Task> history = historyManager.getHistory();

        // Проверяем, что размер истории уменьшился
        assertEquals(4, history.size(), "Размер истории должен быть 4 после удаления из конца");

        // Проверяем порядок оставшихся задач
        assertEquals(task1, history.get(0), "Первая задача должна быть task1");
        assertEquals(task2, history.get(1), "Вторая задача должна быть task2");
        assertEquals(task3, history.get(2), "Третья задача должна быть task3");
        assertEquals(task4, history.get(3), "Четвёртая задача должна быть task4");
    }
}