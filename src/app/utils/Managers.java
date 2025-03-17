package app.utils;

import app.adapter.DurationAdapter;
import app.adapter.LocalDateAdapter;
import app.interfaces.HistoryManager;
import app.interfaces.TaskManager;
import app.service.InMemoryHistoryManager;
import app.service.FileBackedTasksManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public final class Managers {
    private Managers() {

    }

    public static TaskManager getDefault() {
        return new FileBackedTasksManager(new File("resources/bd.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        GsonBuilder gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter());
        return gson.create();
    }
}
