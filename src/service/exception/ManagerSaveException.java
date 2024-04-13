package service.exception;


public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(Exception cause) {
        super(cause);
    }
}
