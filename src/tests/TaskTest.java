package tests;

import app.entities.SubTask;
import app.entities.Task;
import app.enums.TaskStatus;
import app.enums.TaskType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTest {

    @Test
    public void testConstructor() {
        String title = "Test Title";
        String description = "Test Description";
        Task task = new Task(title, description);

        assertEquals(title, task.getTitle(), "Конструктор должен корректно инициализировать поле title");
        assertEquals(description, task.getDescription(), "Конструктор должен корректно инициализировать поле description");
        assertEquals(TaskStatus.NEW, task.getStatus(), "Статус по умолчанию должен быть NEW");
    }

    @Test
    public void testSetTitle() {
        Task task = new Task("Old Title", "Description");
        task.setTitle("New Title");

        assertEquals("New Title", task.getTitle(), "Метод setTitle() должен обновлять название задачи");
    }

    @Test
    public void testSetId() {
        Task task = new Task("Title", "Description");
        task.setId(100);

        assertEquals(100, task.getId(), "Метод setId() должен устанавливать корректный идентификатор задачи");
    }

    @Test
    public void testSetStatus() {
        Task task = new Task("Title", "Description");
        task.setStatus(TaskStatus.IN_PROGRESS);

        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus(), "Метод setStatus() должен обновлять статус задачи");
    }

    @Test
    public void testGetTaskType() {
        Task task = new Task("Title", "Description");

        assertEquals(TaskType.TASK, task.getTaskType(), "Метод getTaskType() должен возвращать TaskType.TASK");
    }

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

        assertEquals(task1, subTask, "Наследники класса Task с одинаковым id должны быть равны");

    }

    @Test
    public void testHashCode() {
        Task task1 = new Task("Title", "Description");
        task1.setId(1);
        Task task2 = new Task("Another Title", "Another Description");
        task2.setId(1);

        assertEquals(task1.hashCode(), task2.hashCode(), "Задачи с одинаковым id должны иметь одинаковый hashCode");
    }

    @Test
    public void testToString() {
        Task task = new Task("Title", "Description");
        task.setId(1);
        task.setStatus(TaskStatus.NEW);

        String expected = "Задача{название='Title', описание='Description', id='1', статус='NEW'";
        assertTrue(task.toString().startsWith(expected), "Метод toString() должен возвращать корректное строковое представление задачи");
    }
}