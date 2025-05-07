package utm.tn.dari.modules.statistiques.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import utm.tn.dari.entities.enums.StatusAnnonce;
import utm.tn.dari.entities.enums.TypeAnnonce;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComparisonResultDTO {
    private double totalAnnoncesDifference;
    private Map<TypeAnnonce, Double> typeCountDifferencePercentage;
    private Map<StatusAnnonce, Double> statusCountDifferencePercentage;
    private double averagePriceDifferencePercentage;
}