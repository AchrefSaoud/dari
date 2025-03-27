package utm.tn.dari.entities;

import java.util.List;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import utm.tn.dari.entities.enums.StatusAnnonce;
import utm.tn.dari.entities.enums.TypeAnnonce;

@Entity
@Data
@NoArgsConstructor
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

    @Enumerated(EnumType.STRING) 
    private TypeAnnonce type;

    @ElementCollection 
    private List<String> videos;

    @ElementCollection 
    private List<String> photos;

    @Enumerated(EnumType.STRING)
    private StatusAnnonce status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bien_immobilier_id")
    @JsonBackReference
    @ToString.Exclude
    private BienImmobilier bienImmobilier;
}