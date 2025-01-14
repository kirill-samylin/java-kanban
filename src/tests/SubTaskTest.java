package tests;

import app.entities.SubTask;
import app.enums.TaskStatus;
import app.enums.TaskType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SubTaskTest {

    @Test
    public void testConstructor() {
        String title = "SubTask Title";
        String description = "SubTask Description";
        int epicId = 42;

        SubTask subTask = new SubTask(title, description, epicId);

        assertEquals(title, subTask.getTitle(), "Конструктор должен корректно инициализировать поле title");
        assertEquals(description, subTask.getDescription(), "Конструктор должен корректно инициализировать поле description");
        assertEquals(TaskStatus.NEW, subTask.getStatus(), "Статус по умолчанию должен быть NEW");
        assertEquals(epicId, subTask.getEpicId(), "Конструктор должен корректно инициализировать поле epicId");
    }

    @Test
    public void testGetEpicId() {
        int epicId = 10;
        SubTask subTask = new SubTask("Title", "Description", epicId);

        assertEquals(epicId, subTask.getEpicId(), "Метод getEpicId() должен возвращать корректный идентификатор эпика");
    }

    @Test
    public void testSetTitle() {
        SubTask subTask = new SubTask("Old Title", "Description", 1);
        subTask.setTitle("New Title");

        assertEquals("New Title", subTask.getTitle(), "Метод setTitle() должен обновлять название задачи");
    }

    @Test
    public void testSetStatus() {
        SubTask subTask = new SubTask("Title", "Description", 1);
        subTask.setStatus(TaskStatus.IN_PROGRESS);

        assertEquals(TaskStatus.IN_PROGRESS, subTask.getStatus(), "Метод setStatus() должен обновлять статус задачи");
    }

    @Test
    public void testSetId() {
        SubTask subTask = new SubTask("Title", "Description", 1);
        subTask.setId(100);

        assertEquals(100, subTask.getId(), "Метод setId() должен устанавливать корректный идентификатор задачи");
    }

    @Test
    public void testGetTaskType() {
        SubTask subTask = new SubTask("Title", "Description", 1);

        assertEquals(TaskType.SUBTASK, subTask.getTaskType(), "Метод getTaskType() должен возвращать TaskType.SUBTASK");
    }

    @Test
    public void testToString() {
        SubTask subTask = new SubTask("Title", "Description", 10);
        subTask.setId(1);
        subTask.setStatus(TaskStatus.NEW);

        String expected = "Подзадача{название='Title', описание='Description', id='1', статус='NEW', id эпика='10'}";

        assertEquals(expected, subTask.toString(), "Метод toString() должен возвращать корректное строковое представление задачи");
    }
}