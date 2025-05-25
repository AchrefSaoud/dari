package utm.tn.dari.modules.eventHandler.dtoes;

import lombok.Data;
import utm.tn.dari.entities.enums.UserInteractionType;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class UserInteractionDTO {
    private Long userId;
    private Long annonceId;
    private Float interactionScore;
    private UserInteractionType interactionType; // e.g., "view", "click", "like", etc.
    private Timestamp interactionDate;

    // You can add more fields as needed to capture user interaction details
}
