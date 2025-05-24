package utm.tn.dari.modules.statistiques.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO pour les statistiques mensuelles
 */
@Data
public class MonthlyStatsDto {
    private String month;
    private int year;
    private int newAbonnements;
    private BigDecimal revenue;
}