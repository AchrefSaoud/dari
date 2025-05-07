package utm.tn.dari.modules.statistiques.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceRangeDTO {
    private double minPrice;
    private double maxPrice;
    private long count;
    private double percentage;
}
