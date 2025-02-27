package app.utils;

import app.interfaces.HistoryManager;
import app.service.InMemoryHistoryManager;
import app.service.FileBackedTasksManager;
import app.service.InMemoryTaskManager;

import java.io.File;

public final class Managers {
    private Managers() {

    }

    public static InMemoryTaskManager getDefault() {
        return new FileBackedTasksManager(new File("resources/bd.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
