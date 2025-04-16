package utm.tn.dari.modules.abonnement.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
@Data
public class RatingCreateDto {
    @NotNull
    private Long abonnementId;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer score;

    @Size(max = 500)
    private String comment;
}