package utm.tn.dari.entities;

import java.util.List;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.springframework.data.geo.Point;
import utm.tn.dari.entities.enums.*;
import utm.tn.dari.modules.location.entities.DemandeLocation;

@Data
@NoArgsConstructor
@Table(name = "annonce")
@Entity
@Builder
@AllArgsConstructor
public class Annonce {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String titre;

    @Column(columnDefinition = "TEXT") 
    private String description;

    @Column(nullable = false)
    private float prix;




    private LeaseDuration leaseDuration;

    @Enumerated(EnumType.STRING)
    private TypeAnnonce type;

    @Enumerated(EnumType.STRING)
    private Rooms rooms = Rooms.ANY;

    private Double latitude;

    private Double longitude;


    @Enumerated(EnumType.STRING)
    private TypeBien typeBien;




    @ElementCollection 
    private List<String> attachmentPaths;




    @Enumerated(EnumType.STRING)
    private StatusAnnonce status;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "annonce")
    @JsonBackReference
    @ToString.Exclude
    private List<DemandeLocation> demandeLocations;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonBackReference
    @ToString.Exclude
    private User user;
}