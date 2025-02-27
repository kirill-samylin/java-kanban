package app.exceptions;

public class ManagerSaveException extends RuntimeException {

    public ManagerSaveException() {
        super();
    }

    public ManagerSaveException(String message, Throwable nativeError) {
        super(message, nativeError);
    }
}