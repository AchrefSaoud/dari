package utm.tn.dari.modules.annonce.Dtoes;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import utm.tn.dari.entities.enums.StatusAnnonce;
import utm.tn.dari.entities.enums.TypeAnnonce;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnnonceDTO {
    Long id;
    String titre;
    String description;
    float prix;
    TypeAnnonce type;
    StatusAnnonce status;
    Long userId;
    Double longitude;
    Double latitude;
    List<String> attachmentPaths;



}
