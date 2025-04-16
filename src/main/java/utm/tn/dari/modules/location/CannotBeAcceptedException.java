package utm.tn.dari.modules.location;

public class CannotBeAcceptedException extends Exception{
    public CannotBeAcceptedException(String message) {
        super(message);
    }

    public CannotBeAcceptedException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotBeAcceptedException(Throwable cause) {
        super(cause);
    }
}
