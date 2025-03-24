package app.http.handlers;

import app.interfaces.TaskManager;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {
    public PrioritizedHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            String method = exchange.getRequestMethod();

            switch (method) {
                case "GET":
                    handleGet(exchange);
                    break;
                default:
                    sendNotFound(exchange, "Неподдерживаемый метод: " + method);
            }
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String response = gson.toJson(manager.getPrioritizedTasks());
        sendText(exchange, response);
    }
}