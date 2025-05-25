package utm.tn.dari.modules.annonce.Dtoes;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserEvent {
    private Long userId;
    private UserEventType userEventType;
    private AnnonceDTO annonceDTO;

}
