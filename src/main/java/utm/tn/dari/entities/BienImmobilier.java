package utm.tn.dari.entities;

import java.util.List;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BienImmobilier {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bienImm_id;
    
    private String localisation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrat_id")
    @JsonBackReference
    @ToString.Exclude
    private Contrat contrat;
/*
    @OneToMany(mappedBy = "bienImmobilier", fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    private List<Visite> visites;
*/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proprietaire_id")
    @JsonBackReference
    @ToString.Exclude
    private User proprietaire;


}