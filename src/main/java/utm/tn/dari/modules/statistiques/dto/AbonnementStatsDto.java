package utm.tn.dari.modules.statistiques.dto;

import lombok.Data;
import utm.tn.dari.entities.enums.TypeAbonnement;

import java.math.BigDecimal;
import java.util.Map;

/**
 * DTO pour les statistiques globales d'abonnements
 */
@Data
public class AbonnementStatsDto {
    // Nombre d'abonnements total, actifs et inactifs
    private int totalAbonnements;
    private int activeAbonnements;
    private int inactiveAbonnements;

    // Répartition par type
    private Map<TypeAbonnement, Integer> abonnementsByType;

    // Prix moyen
    private BigDecimal averagePrice;

    // Type le plus populaire
    private TypeAbonnement mostPopularType;

    // Revenu total
    private BigDecimal totalRevenue;
}