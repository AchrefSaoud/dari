package utm.tn.dari.modules.statistiques.dto.statistics;

import  lombok.AllArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import utm.tn.dari.entities.enums.StatusAnnonce;
import utm.tn.dari.entities.enums.TypeAnnonce;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;



/**
 * DTO for general statistics about announcements
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class GeneralStatisticsDTO {
    private long totalAnnouncements;
    private double averagePrice;
    private Map<StatusAnnonce, Long> statusCounts;
    private double percentageActive;
    private LocalDate lastUpdateDate;
}
