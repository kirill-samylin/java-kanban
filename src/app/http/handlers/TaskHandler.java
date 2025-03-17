package app.http.handlers;

import app.entities.Task;
import app.exceptions.TaskOverlapException;
import app.interfaces.TaskManager;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TaskHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath().substring(1);
            String[] segments = path.split("/");

            switch (method) {
                case "GET":
                    handleGet(exchange, segments);
                    break;
                case "POST":
                    handlePost(exchange, segments);
                    break;
                case "DELETE":
                    handleDelete(exchange, segments);
                    break;
                default:
                    sendNotFound(exchange, "Неподдерживаемый метод: " + method);
            }
        }
    }

    private void handleGet(HttpExchange exchange, String[] segments) throws IOException {
        if (segments.length == 2) {
            int id = Integer.parseInt(segments[1]);
            Task task = manager.getTask(id);
            if (task == null) {
                String errorMessage = "Задача с таким номером id не найдена";
                sendNotFound(exchange, errorMessage);
            } else {
                String response = gson.toJson(task);
                sendText(exchange, response);
            }
        } else {
            String response = gson.toJson(manager.getTasks());
            sendText(exchange, response);
        }
    }

    private void handlePost(HttpExchange exchange, String[] segments) throws IOException {
        InputStream requestBody = exchange.getRequestBody();
        String body = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
        Task jsonTask = gson.fromJson(body, Task.class);
        try {
            if (segments.length == 2) {
                manager.updateTask(jsonTask);
                sendText(exchange, "Задача успешно обновлена.", 201);
            } else {
                manager.createNewTask(jsonTask);
                sendText(exchange, "Задача успешно создана", 201);
            }
        } catch (TaskOverlapException e) {
            sendHasInteractions(exchange, e.getMessage());
        }
    }

    private void handleDelete(HttpExchange exchange, String[] segments) throws IOException {
        if (segments.length == 2) {
            int id = Integer.parseInt(segments[1]);
            Task task = manager.getTask(id);
            if (task == null) {
                String errorMessage = "Задача с таким номером id не найдена";
                sendNotFound(exchange, errorMessage);
            } else {
                manager.removeTaskById(id);
                sendText(exchange, "Задача успешно удалена");
            }
        } else {
            String errorMessage = "Необходимо указать id";
            sendNotFound(exchange, errorMessage);
        }
    }
}