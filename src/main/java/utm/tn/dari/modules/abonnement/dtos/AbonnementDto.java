package utm.tn.dari.modules.abonnement.dtos;

import java.math.BigDecimal;

import lombok.Data;
import utm.tn.dari.entities.enums.TypeAbonnement;

@Data
public class AbonnementDto {
    private Long id;
    private String nom;
    private String description;
    private BigDecimal prix;
    private TypeAbonnement type;
}