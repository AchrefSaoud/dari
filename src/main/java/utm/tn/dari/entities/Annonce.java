package utm.tn.dari.entities;

import java.util.List;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.geo.Point;
import utm.tn.dari.entities.enums.Rooms;
import utm.tn.dari.entities.enums.StatusAnnonce;
import utm.tn.dari.entities.enums.TypeAnnonce;
import utm.tn.dari.entities.enums.TypeBien;
import utm.tn.dari.modules.location.entities.DemandeLocation;

@Data
@NoArgsConstructor
@Table(name = "annonce")
@Entity

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



    @Enumerated(EnumType.STRING)
    private TypeAnnonce type;

    @Enumerated(EnumType.STRING)
    private Rooms rooms = Rooms.Any;

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