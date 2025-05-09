package utm.tn.dari.modules.annonce.Dtoes;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import utm.tn.dari.entities.enums.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnnonceDTO {
    @Schema(hidden = true)

    Long id;
    String titre;
    String description;
    float prix;
    LeaseDuration leaseDuration;
    Rooms rooms;
    TypeAnnonce type;
    TypeBien typeBien;
    StatusAnnonce status;
    Long userId;
    Double longitude;
    Double latitude;
    String imagePath ;
    @Schema(hidden = true)
    List<String> attachmentPaths;



}
