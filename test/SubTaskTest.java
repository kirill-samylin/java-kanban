import app.entities.SubTask;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SubTaskTest {

    @Test
    public void testGetEpicId() {
        int epicId = 10;
        SubTask subTask = new SubTask("Title", "Description", epicId);

        assertEquals(epicId, subTask.getEpicId(), "Метод getEpicId() должен возвращать корректный идентификатор эпика");
    }

    @Test
    public void testSetId() {
        SubTask subTask = new SubTask("Название задачи", "Описание задачи", 5005);

        // Пытаемся установить id, равное epicId
        subTask.setId(5005);

        // Проверяем, что id не был изменен на epicId
        assertNotEquals(5005, subTask.getId());

        // Проверяем, что id осталось прежним (по умолчанию, предположим, 0)
        assertEquals(0, subTask.getId());

        // Дополнительно можно проверить, что было выведено сообщение в консоль
        // Однако тестирование вывода в консоль требует дополнительного инструментария
    }
}