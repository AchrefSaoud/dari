package utm.tn.dari.modules.meuble.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PanierDTO {
    private Long id;
    private Long acheteurId;
    private List<PanierItemDTO> items = new ArrayList<>();
    private float total;
}
