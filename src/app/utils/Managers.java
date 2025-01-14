package app.utils;

import app.interfaces.HistoryManager;
import app.interfaces.TaskManager;
import app.service.InMemoryHistoryManager;
import app.service.InMemoryTaskManager;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
