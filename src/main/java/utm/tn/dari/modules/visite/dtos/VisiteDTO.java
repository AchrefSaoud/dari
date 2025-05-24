package utm.tn.dari.modules.visite.dtos;

import lombok.Builder;
import lombok.Data;
import utm.tn.dari.entities.Visite;

import java.time.LocalDateTime;
@Data

public class VisiteDTO {
    private Long id;
    private Long ownerId; // Only store ID instead of whole object
    private String ownerName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String clientName;

    // other fields...

    // constructor from Visite entity
    public VisiteDTO(Visite visite) {
        this.id = visite.getId();
        this.ownerId = visite.getOwner().getId();
        this.ownerName = visite.getOwner().getUsername();
        this.startTime = visite.getStartTime();
        this.endTime = visite.getEndTime();
        this.clientName = visite.getClient() != null ? visite.getClient().getUsername() : "null";

        // other fields
    }
    // getters
}