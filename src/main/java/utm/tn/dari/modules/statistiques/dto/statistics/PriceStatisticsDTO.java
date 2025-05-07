package utm.tn.dari.modules.statistiques.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import utm.tn.dari.entities.enums.TypeAnnonce;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class PriceStatisticsDTO {
    private TypeAnnonce type;
    private double minimumPrice;
    private double maximumPrice;
    private double averagePrice;
    private double medianPrice;
    private List<PriceRangeDTO> priceRanges;
}
