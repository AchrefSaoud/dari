package utm.tn.dari.modules.annonce.exceptions;

public class FileSavingException extends Exception{
    private String message;

    public FileSavingException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
