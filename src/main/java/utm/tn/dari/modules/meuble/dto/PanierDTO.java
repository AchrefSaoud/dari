package utm.tn.dari.modules.meuble.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PanierDTO {
    /* private Long id; */
    @NotNull
    @JsonProperty("acheteurId")
    private Long acheteurId;
    
    private List<PanierItemDTO> items;
    private float total;
}
