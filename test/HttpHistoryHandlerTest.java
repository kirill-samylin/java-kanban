import app.entities.SubTask;
import app.exceptions.TaskOverlapException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpHistoryHandlerTest extends HttpBaseHandlerTest {
    public HttpHistoryHandlerTest() throws IOException {
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
}