package utm.tn.dari.modules.meuble.dto;
import lombok.Data;

@Data
public class PanierItemDTO {
    private Long meubleId;
    private int quantite;
    private float sousTotal;
}


