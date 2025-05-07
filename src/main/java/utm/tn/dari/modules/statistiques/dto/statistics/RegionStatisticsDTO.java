package utm.tn.dari.modules.statistiques.dto.statistics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegionStatisticsDTO {
    private String region;
    private long announcementCount;
    private double averagePrice;
}
