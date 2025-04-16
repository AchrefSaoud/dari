package utm.tn.dari.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meuble {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String description;
    private float prix;
    private String adresse;
    private String photoUrl;

    @ManyToOne
    @JoinColumn(name = "vendeur_id")
    private User vendeur;
}