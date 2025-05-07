package utm.tn.dari.modules.statistiques.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import utm.tn.dari.entities.enums.StatusAnnonce;
import utm.tn.dari.entities.enums.TypeAnnonce;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeDataPointDTO {
    private LocalDate date;
    private long totalCount;
    private Map<TypeAnnonce, Long> countByType;
    private Map<StatusAnnonce, Long> countByStatus;
    private double averagePrice;
}