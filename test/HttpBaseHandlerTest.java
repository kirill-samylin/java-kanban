import app.entities.Epic;
import app.entities.SubTask;
import app.entities.Task;
import app.enums.TaskStatus;
import app.http.HttpTaskServer;
import app.interfaces.TaskManager;
import app.utils.Managers;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpBaseHandlerTest {
    protected SubTask createSubtask() {
        return new SubTask("Подзадача", "Описание подзадачи", 1, TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5), 2);
    }

    protected Epic createEpic() {
        return new Epic("Эпик", "Описание эпика", 2);
    }

    protected Task createTask() {
        return new Task("Задача", "Описание подзадачи",3, TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
    }

    protected HttpClient client;
    protected TaskManager manager;
    protected HttpTaskServer taskServer;
    protected Gson gson;

    public HttpBaseHandlerTest() throws IOException {
        this.manager = Managers.getDefault();
        this.gson = Managers.getGson();
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
}
