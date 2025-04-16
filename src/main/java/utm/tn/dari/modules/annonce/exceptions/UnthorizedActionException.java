package utm.tn.dari.modules.annonce.exceptions;

public class UnthorizedActionException extends Exception {
    private String message;

    public UnthorizedActionException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
