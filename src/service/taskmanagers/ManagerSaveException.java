package service.taskmanagers;

import java.io.IOException;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(Throwable cause) {
        super(cause);
    }

    public ManagerSaveException() {
    }
}
