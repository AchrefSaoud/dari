package utm.tn.dari.modules.annonce.exceptions;


import lombok.Data;

@Data
public class ObjectNotFoundException extends Exception{
    String message;
    public ObjectNotFoundException(String message) {
        super(message);
        this.message = message;
    }
}
