package utm.tn.dari.modules.meuble.dto;

import lombok.Data;

import java.util.List;

@Data
public class PanierDTO {
    private Long acheteurId;
    private List<Long> meubleIds;
}
