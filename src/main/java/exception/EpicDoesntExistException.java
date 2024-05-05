package exception;

public class EpicDoesntExistException extends RuntimeException {
    public EpicDoesntExistException(String message) {
        super(message);
    }
}
