package utm.tn.dari.modules.location.dtoes;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import utm.tn.dari.entities.enums.DemandeLocationStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class DemandeLocationDTO {
    private Long id;
    private Long annonceId;
    private Long userId;
    private DemandeLocationStatus status;
    private LocalDateTime dateDemande;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String message;
    private String lettreEng;
    private String justifcatifPaiementdeCaution;
    private List<String> fichesDePaies;


}
