package utm.tn.dari.modules.statistiques.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class GeographicStatisticsDTO {
    private List<RegionStatisticsDTO> regionStatistics; // Changed from 'regions' to 'regionStatistics'
    private long totalAnnouncements;
}
