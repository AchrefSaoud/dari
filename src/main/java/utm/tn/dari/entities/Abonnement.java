package utm.tn.dari.entities;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import utm.tn.dari.entities.enums.TypeAbonnement;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @OneToMany(mappedBy = "abonnement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings = new ArrayList<>();
    @OneToMany(mappedBy = "abonnement", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<User> users;
    private LocalDate date; // nouvelle colonne date

    @PrePersist
    public void prePersist() {
        if (date == null) {
            date = LocalDate.now(); // définit automatiquement la date si elle n’est pas fournie
        }
    }
}