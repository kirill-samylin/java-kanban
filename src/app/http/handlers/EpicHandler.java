package app.http.handlers;

import app.entities.Epic;
import app.interfaces.TaskManager;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class EpicHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public EpicHandler(TaskManager manager) {
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
        if (segments.length == 2 || segments.length == 3) {
            int id = Integer.parseInt(segments[1]);
            Epic epic = manager.getEpic(id);
            if (epic == null) {
                String errorMessage = "Эпик с таким номером id не найдена";
                sendNotFound(exchange, errorMessage);
            } else {
                if (segments.length == 2) {
                    String response = gson.toJson(epic);
                    sendText(exchange, response);
                } else {
                    String response = gson.toJson(manager.getSubTasksByEpic(id));
                    sendText(exchange, response);
                }
            }
        } else {
            String response = gson.toJson(manager.getEpics());
            sendText(exchange, response);
        }
    }

    private void handlePost(HttpExchange exchange, String[] segments) throws IOException {
        InputStream requestBody = exchange.getRequestBody();
        String body = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
        Epic jsonEpic = gson.fromJson(body, Epic.class);
        if (segments.length == 2) {
            manager.updateEpic(jsonEpic);
            sendText(exchange, "Эпик успешно обновлена.", 201);
        } else {
            manager.createNewEpic(jsonEpic);
            sendText(exchange, "Эпик успешно создана", 201);
        }
    }

    private void handleDelete(HttpExchange exchange, String[] segments) throws IOException {
        if (segments.length == 2) {
            int id = Integer.parseInt(segments[1]);
            Epic epic = manager.getEpic(id);
            if (epic == null) {
                String errorMessage = "Эпик с таким номером id не найдена";
                sendNotFound(exchange, errorMessage);
            } else {
                manager.removeEpicById(id);
                sendText(exchange, "Эпик успешно удалена");
            }
        } else {
            String errorMessage = "Необходимо указать id";
            sendNotFound(exchange, errorMessage);
        }
    }
}