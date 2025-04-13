package utm.tn.dari.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RechercheAchat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String localisation;

    private Float minPrix;

    private Float maxPrix;

    private Integer surfaceMin;

    private Integer surfaceMax;

    private String typeBien; // maison, appartement, etc.

    private boolean notifier = true;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAjout = new Date();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}

