package utm.tn.dari.modules.location.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import utm.tn.dari.entities.Annonce;
import utm.tn.dari.entities.User;
import utm.tn.dari.entities.enums.DemandeLocationStatus;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class DemandeLocation {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(targetEntity = Annonce.class)
    private Annonce annonce;

    @ManyToOne(targetEntity = User.class)
    private User user;

    private String message;

    private DemandeLocationStatus status;

    @CreationTimestamp()
    private LocalDateTime createdAt;

    @ElementCollection
    private List<String> fichesDePaies;

    private String lettreEng;

    private String justifcatifPaiementdeCaution;


    private LocalDateTime dateDebutLocation;

    private LocalDateTime dateFinLocation;


}
