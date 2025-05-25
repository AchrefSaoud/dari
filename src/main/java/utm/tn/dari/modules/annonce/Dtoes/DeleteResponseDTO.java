package utm.tn.dari.modules.annonce.Dtoes;

import lombok.Builder;

@Builder
public class DeleteResponseDTO {
    private String message;

    public DeleteResponseDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
