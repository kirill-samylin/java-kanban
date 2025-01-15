import app.entities.Epic;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {

    @Test
    public void testGetSubTaskIds() {
        Epic epic = new Epic("Epic", "Description");
        ArrayList<Integer> subTaskIds = new ArrayList<>(Arrays.asList(1, 2, 3));
        epic.setSubTaskIds(subTaskIds);

        ArrayList<Integer> retrievedSubTaskIds = epic.getSubTaskIds();

        assertEquals(subTaskIds, retrievedSubTaskIds, "Метод getSubTaskIds() должен возвращать корректный список идентификаторов подзадач");
        assertNotSame(subTaskIds, retrievedSubTaskIds, "getSubTaskIds() должен возвращать копию списка, а не оригинал");
    }

    @Test
    public void testSetSubTaskIds() {
        Epic epic = new Epic("Epic", "Description");
        ArrayList<Integer> subTaskIds = new ArrayList<>(Arrays.asList(4, 5, 6));
        epic.setSubTaskIds(subTaskIds);

        assertEquals(subTaskIds, epic.getSubTaskIds(), "Метод setSubTaskIds() должен корректно устанавливать список идентификаторов подзадач");
    }

    @Test
    public void testAddSubTaskId() {
        Epic epic = new Epic("Epic", "Description");
        epic.setId(100);
        epic.addSubTaskId(101);

        assertEquals(1, epic.getSubTaskIds().size(), "addSubTaskId() должен добавлять идентификатор подзадачи в список");
        assertTrue(epic.getSubTaskIds().contains(101), "Список подзадач должен содержать добавленный идентификатор");

        epic.addSubTaskId(100); // Попытка добавить идентификатор эпика в его же подзадачи

        assertEquals(1, epic.getSubTaskIds().size(), "Не должен добавляться идентификатор эпика в список подзадач");
        assertFalse(epic.getSubTaskIds().contains(100), "Список подзадач не должен содержать идентификатор самого эпика");
    }

    @Test
    public void testRemoveSubTaskId() {
        Epic epic = new Epic("Epic", "Description");
        epic.setSubTaskIds(new ArrayList<>(Arrays.asList(1, 2, 3)));

        epic.removeSubTaskId(2);

        assertEquals(2, epic.getSubTaskIds().size(), "removeSubTaskId() должен удалять идентификатор подзадачи из списка");
        assertFalse(epic.getSubTaskIds().contains(2), "Список подзадач не должен содержать удалённый идентификатор");
    }
}