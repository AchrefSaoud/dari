package utm.tn.dari.modules.annonce.Dtoes;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import utm.tn.dari.entities.enums.Rooms;
import utm.tn.dari.entities.enums.StatusAnnonce;
import utm.tn.dari.entities.enums.TypeAnnonce;
import utm.tn.dari.entities.enums.TypeBien;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class USearchQueryDTO {
    private String query;
    private Float minPrix;
    private Float maxPrix;
    private TypeAnnonce type;
    private TypeBien typeBien;
    private Rooms rooms;
    private StatusAnnonce statusAnnonce;
    private Double latitude;
    private Double longitude;
    private Double radius;
    private LocalDateTime createdAt;
    private Long userId;

}
