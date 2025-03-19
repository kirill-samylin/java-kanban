import app.entities.Epic;
import app.entities.SubTask;
import app.enums.TaskStatus;
import app.exceptions.TaskOverlapException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpEpicHandlerTest extends HttpBaseHandlerTest {
    public HttpEpicHandlerTest() throws IOException {
    }

    @Test
    void testCreateEpic() throws IOException, InterruptedException {
        Epic epic = createEpic();
        String epicJson = gson.toJson(epic);
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Epic> epicsFromManager = manager.getEpics();
        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Эпик", epicsFromManager.getFirst().getTitle(), "Некорректное имя эпика");
    }

    @Test
    void testGetEpics() throws IOException, TaskOverlapException, InterruptedException {
        Epic epic = createEpic();
        manager.createNewEpic(epic);
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void testGetEpicById() throws IOException, TaskOverlapException, InterruptedException {
        Epic epic = createEpic();
        int id = manager.createNewEpic(epic);
        URI url = URI.create("http://localhost:8080/epics/"+id);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(),"Эпик по id не найдена");
    }

    @Test
    void testUpdateEpic() throws IOException, TaskOverlapException, InterruptedException {
        Epic epic = createEpic();
        manager.createNewEpic(epic);
        epic.setStatus(TaskStatus.IN_PROGRESS);

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        URI url2 = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .uri(url2)
                .build();
        HttpResponse<String> response = client.send(request2, handler);
        assertEquals(201, response.statusCode(), "Неккоректно обновляет эпик");
    }

    @Test
    void testNotFoundEpic() throws IOException, TaskOverlapException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics/" + 5);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(404, response.statusCode(), "Неккоректно возвращает код 404");
    }

    @Test
    void testNotFoundEpicSubtask() throws IOException, TaskOverlapException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics/" + 5 + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(404, response.statusCode(), "Неккоректно возвращает код 404");
    }

    @Test
    void testDeleteEpic() throws IOException, TaskOverlapException, InterruptedException {
        Epic epic = createEpic();
        manager.createNewEpic(epic);
        URI url2 = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpRequest request2 = HttpRequest.newBuilder()
                .DELETE()
                .uri(url2)
                .build();
        HttpResponse.BodyHandler<String> handler2 = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response2 = client.send(request2, handler2);
        assertEquals(200, response2.statusCode(), "Неккоректно удаляет задачу");
    }

    @Test
    void testGetEpicsSubTaskIds() throws IOException, TaskOverlapException, InterruptedException {
        Epic epic = createEpic();
        SubTask subTask = createSubtask();
        manager.createNewEpic(epic);
        manager.createNewSubtask(subTask);
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId() + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }
}