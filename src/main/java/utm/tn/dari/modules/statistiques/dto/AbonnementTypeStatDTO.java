package utm.tn.dari.modules.statistiques.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AbonnementTypeStatDTO {
    private String type;
    private Long count;
}
