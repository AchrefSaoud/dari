package utm.tn.dari.modules.statistiques.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import utm.tn.dari.entities.enums.TypeAnnonce;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor


public class TypeStatisticsDTO {
    private Map<TypeAnnonce, Long> distributionByType;
    private Map<TypeAnnonce, Double> averagePriceByType;
    private long totalAnnouncements;
}
