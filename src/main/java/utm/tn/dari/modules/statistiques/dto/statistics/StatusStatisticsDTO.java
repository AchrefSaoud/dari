package utm.tn.dari.modules.statistiques.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import utm.tn.dari.entities.enums.StatusAnnonce;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class StatusStatisticsDTO {
    private Map<StatusAnnonce, Long> distributionByStatus;
    private Map<StatusAnnonce, Double> percentageByStatus;
    private long totalAnnouncements;
}
