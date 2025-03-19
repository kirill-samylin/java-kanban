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

public class HttpPrioritizedHandledTest extends HttpBaseHandlerTest {
    public HttpPrioritizedHandledTest() throws IOException {
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