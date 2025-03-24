package app.http.handlers;

import app.entities.SubTask;
import app.exceptions.TaskOverlapException;
import app.interfaces.TaskManager;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SubTaskHandler extends BaseHttpHandler {
    public SubTaskHandler(TaskManager manager) {
        super(manager);
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
            SubTask subTask = manager.getSubTask(id);
            if (subTask == null) {
                String errorMessage = "Подзадача с таким номером id не найдена";
                sendNotFound(exchange, errorMessage);
            } else {
                String response = gson.toJson(subTask);
                sendText(exchange, response);
            }
        } else {
            String response = gson.toJson(manager.getSubTasks());
            sendText(exchange, response);
        }
    }

    private void handlePost(HttpExchange exchange, String[] segments) throws IOException {
        InputStream requestBody = exchange.getRequestBody();
        String body = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
        SubTask jsonSubTask = gson.fromJson(body, SubTask.class);
        try {
            if (segments.length == 2) {
                manager.updateSubTask(jsonSubTask);
                sendText(exchange, "Подзадача успешно обновлена.", 201);
            } else {
                manager.createNewSubtask(jsonSubTask);
                sendText(exchange, "Подзадача успешно создана", 201);
            }
        } catch (TaskOverlapException e) {
            sendHasInteractions(exchange, e.getMessage());
        }
    }

    private void handleDelete(HttpExchange exchange, String[] segments) throws IOException {
        if (segments.length == 2) {
            int id = Integer.parseInt(segments[1]);
            SubTask subTask = manager.removeSubTaskById(id);
            if (subTask == null) {
                String errorMessage = "Подзадача с таким номером id не найдена";
                sendNotFound(exchange, errorMessage);
            } else {
                sendText(exchange, "Подзадача успешно удалена");
            }
        } else {
            String errorMessage = "Необходимо указать id";
            sendNotFound(exchange, errorMessage);
        }
    }
}