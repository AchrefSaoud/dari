package utm.tn.dari.modules.statistiques.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class TimeSeriesStatisticsDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private TimeInterval interval;
    private Map<LocalDate, Long> announcementCounts;
}
