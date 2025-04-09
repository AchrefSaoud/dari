package utm.tn.dari.entities;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import utm.tn.dari.entities.enums.TypeAbonnement;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
@Data
@Entity
@NoArgsConstructor

public class Abonnement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du paquet est obligatoire")
    private String nom;

    private String description;

    private BigDecimal prix; // Prix de l'abonnement

    @Enumerated(EnumType.STRING)
    private TypeAbonnement type; // BASIC, STANDARD, PREMIUM
}