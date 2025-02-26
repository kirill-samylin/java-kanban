import app.entities.SubTask;
import app.entities.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {
    @Test
    public void testEquals() {
        Task task1 = new Task("Title", "Description");
        task1.setId(1);
        Task task2 = new Task("Another Title", "Another Description");
        task2.setId(1);
        Task task3 = new Task("Title", "Description");
        task3.setId(2);

        assertEquals(task1, task2, "Задачи с одинаковым id должны быть равны");
        assertNotEquals(task1, task3, "Задачи с разными id не должны быть равны");

        Task subTask = new SubTask("Title", "Desciprion", 99);
        subTask.setId(1);

        assertFalse(task1.equals(subTask), "Наследники класса Task с одинаковым id не должны быть равны");
    }

    @Test
    public void testHashCode() {
        Task task1 = new Task("Title", "Description");
        task1.setId(1);
        Task task2 = new Task("Another Title", "Another Description");
        task2.setId(1);

        assertEquals(task1.hashCode(), task2.hashCode(), "Задачи с одинаковым id должны иметь одинаковый hashCode");
    }
}