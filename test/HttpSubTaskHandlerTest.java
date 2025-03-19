import app.entities.Epic;
import app.entities.SubTask;
import app.entities.Task;
import app.enums.TaskStatus;
import app.exceptions.TaskOverlapException;
import app.http.HttpTaskServer;
import app.interfaces.TaskManager;
import app.utils.Managers;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpSubTaskHandlerTest extends HttpBaseHandlerTest {
    public HttpSubTaskHandlerTest() throws IOException {
    }

    @Test
    void testCreateSubTask() throws IOException, InterruptedException {
        SubTask subTask = createSubtask();
        String subTaskJson = gson.toJson(subTask);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<SubTask> subTasksFromManager = manager.getSubTasks();

        assertNotNull(subTasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subTasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Подзадача", subTasksFromManager.getFirst().getTitle(), "Некорректное имя подзадачи");
    }

    @Test
    void testGetSubTasks() throws IOException, TaskOverlapException, InterruptedException {
        SubTask subTask = createSubtask();
        manager.createNewSubtask(subTask);
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void testGetSubTaskById() throws IOException, TaskOverlapException, InterruptedException {
        SubTask subTask = createSubtask();
        int id = manager.createNewSubtask(subTask);
        URI url = URI.create("http://localhost:8080/subtasks/"+id);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(),"Подзадача по id не найдена");
    }

    @Test
    void testSubTaskOverlapException() throws IOException, TaskOverlapException, InterruptedException {
        SubTask subTask = createSubtask();
        manager.createNewSubtask(subTask);
        // Создаем задачу с пересечением
        SubTask subTask1 = createSubtask();
        subTask1.setId(55);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask1)))
                .uri(URI.create("http://localhost:8080/subtasks"))
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(406, response.statusCode(), "Неккоретно отрабатывает ошибка о пересечении по дате");
    }

    @Test
    void testUpdateSubTask() throws IOException, TaskOverlapException, InterruptedException {
        SubTask subTask = createSubtask();
        manager.createNewSubtask(subTask);
        subTask.setStatus(TaskStatus.IN_PROGRESS);
        URI url = URI.create("http://localhost:8080/subtasks/" + subTask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask)))
                .uri(url)
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(201, response.statusCode(), "Неккоректно обновляет подзадачу");
    }

    @Test
    void testNotFoundSubTask() throws IOException, TaskOverlapException, InterruptedException {
        URI url = URI.create("http://localhost:8080/subtasks/" + 5);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(404, response.statusCode(), "Неккоректно возвращает код 404");
    }

    @Test
    void testDeleteSubTask() throws IOException, TaskOverlapException, InterruptedException {
        SubTask subTask = createSubtask();
        manager.createNewSubtask(subTask);

        URI url2 = URI.create("http://localhost:8080/subtasks/" + subTask.getId());
        HttpRequest request2 = HttpRequest.newBuilder()
                .DELETE()
                .uri(url2)
                .build();
        HttpResponse.BodyHandler<String> handler2 = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response2 = client.send(request2, handler2);
        assertEquals(200, response2.statusCode(), "Неккоректно удаляет задачу");
    }
}