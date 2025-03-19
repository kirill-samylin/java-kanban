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

public class HttpTaskHandlerTest extends HttpBaseHandlerTest {

    public HttpTaskHandlerTest() throws IOException {
    }

    @Test
    void testCreateTask() throws IOException, TaskOverlapException, InterruptedException  {
        Task task = createTask();
        String taskJson = gson.toJson(task);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Задача", tasksFromManager.getFirst().getTitle(), "Некорректное имя задачи");
    }

    @Test
    void testGetTasks() throws IOException, TaskOverlapException, InterruptedException {
        Task task = createTask();
        manager.createNewTask(task);
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode() );
    }

    @Test
    void testGetTasksById() throws IOException, TaskOverlapException, InterruptedException {
        Task task = createTask();
        int id = manager.createNewTask(task);
        URI url = URI.create("http://localhost:8080/tasks/"+id);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200,response.statusCode(),"Задача по id не найдена");
    }

    @Test
    void testTaskOverlapException() throws IOException, TaskOverlapException, InterruptedException {
        Task task = createTask();
        manager.createNewTask(task);
        // Создаем задачу с пересечением
        Task task1 = createTask();
        task1.setId(55);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .uri(url)
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(406, response.statusCode(), "Неккоретно отрабатывает ошибка о пересечении по дате");
    }

    @Test
    void testUpdateTask() throws IOException, TaskOverlapException, InterruptedException {
        Task task = createTask();
        manager.createNewTask(task);

        task.setStatus(TaskStatus.IN_PROGRESS);
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .uri(url)
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(201, response.statusCode(), "Неккоректно обновляет задачу");
    }

    @Test
    void testNotFoundTask() throws IOException, TaskOverlapException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/" + 5);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(404, response.statusCode(), "Неккоректно возвращает код 404");
    }

    @Test
    void testDeleteTask() throws IOException, TaskOverlapException, InterruptedException {
        Task task = createTask();
        manager.createNewTask(task);
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(200, response.statusCode(), "Неккоректно удаляет задачу");
    }
}