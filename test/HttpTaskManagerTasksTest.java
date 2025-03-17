import app.entities.Epic;
import app.entities.SubTask;
import app.entities.Task;
import app.enums.TaskStatus;
import app.exceptions.TaskOverlapException;
import app.http.HttpTaskServer;
import app.interfaces.TaskManager;
import app.utils.Managers;
import com.google.gson.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

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

public class HttpTaskManagerTasksTest {
    protected SubTask createSubtask() {
        return new SubTask("Подзадача", "Описание подзадачи", 1, TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5), 2);
    }
    protected Epic createEpic() {
        return new Epic("Эпик", "Описание эпика", 2);
    }
    protected Task createTask() {
        return new Task("Задача", "Описание подзадачи",3, TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
    }

    HttpClient client;
    TaskManager manager = Managers.getDefault();
    HttpTaskServer taskServer;
    Gson gson = Managers.getGson();

    public HttpTaskManagerTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException {
        manager = Managers.getDefault();
        client = HttpClient.newHttpClient();
        taskServer = new HttpTaskServer(manager);
        manager.removeAllTasks();
        manager.removeAllEpic();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
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
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .uri(url)
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(201, response.statusCode());
        // Создаем задачу с пересечением
        Task task1 = createTask();
        task1.setId(55);
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .uri(URI.create("http://localhost:8080/tasks"))
                .build();
        HttpResponse.BodyHandler<String> handler1 = HttpResponse.BodyHandlers.ofString();
        response = client.send(request, handler1);
        assertEquals(406, response.statusCode(), "Неккоретно отрабатывает ошибка о пересечении по дате");
    }

    @Test
    void testUpdateTask() throws IOException, TaskOverlapException, InterruptedException {
        Task task = createTask();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .uri(url)
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(201, response.statusCode());

        task.setStatus(TaskStatus.IN_PROGRESS);
        URI url2 = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .uri(url2)
                .build();
        HttpResponse.BodyHandler<String> handler1 = HttpResponse.BodyHandlers.ofString();
        response = client.send(request2, handler1);
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
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .uri(url)
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(201, response.statusCode());

        URI url2 = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request2 = HttpRequest.newBuilder()
                .DELETE()
                .uri(url2)
                .build();
        HttpResponse.BodyHandler<String> handler2 = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response2 = client.send(request2, handler2);
        assertEquals(200, response2.statusCode(), "Неккоректно удаляет задачу");
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
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask)))
                .uri(url)
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(201, response.statusCode());
        // Создаем задачу с пересечением
        SubTask subTask1 = createSubtask();
        subTask1.setId(55);
        request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask1)))
                .uri(URI.create("http://localhost:8080/subtasks"))
                .build();
        response = client.send(request, handler);
        assertEquals(406, response.statusCode(), "Неккоретно отрабатывает ошибка о пересечении по дате");
    }

    @Test
    void testUpdateSubTask() throws IOException, TaskOverlapException, InterruptedException {
        SubTask subTask = createSubtask();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask)))
                .uri(url)
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(201, response.statusCode());

        subTask.setStatus(TaskStatus.IN_PROGRESS);
        URI url2 = URI.create("http://localhost:8080/subtasks/" + subTask.getId());
        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask)))
                .uri(url2)
                .build();
        response = client.send(request2, handler);
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
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask)))
                .uri(url)
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(201, response.statusCode());

        URI url2 = URI.create("http://localhost:8080/subtasks/" + subTask.getId());
        HttpRequest request2 = HttpRequest.newBuilder()
                .DELETE()
                .uri(url2)
                .build();
        HttpResponse.BodyHandler<String> handler2 = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response2 = client.send(request2, handler2);
        assertEquals(200, response2.statusCode(), "Неккоректно удаляет задачу");
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
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .uri(url)
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(201, response.statusCode());

        epic.setStatus(TaskStatus.IN_PROGRESS);
        URI url2 = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .uri(url2)
                .build();
        response = client.send(request2, handler);
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
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .uri(url)
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        assertEquals(201, response.statusCode());

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

    @Test
    void testGetHistory() throws IOException, TaskOverlapException, InterruptedException {
        SubTask subTask = createSubtask();
        manager.createNewSubtask(subTask);
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void testGetPrioritized() throws IOException, TaskOverlapException, InterruptedException {
        SubTask subTask = createSubtask();
        manager.createNewSubtask(subTask);
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }
}