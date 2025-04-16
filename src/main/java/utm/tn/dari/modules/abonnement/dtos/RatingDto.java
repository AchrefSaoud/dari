package utm.tn.dari.modules.abonnement.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingDto {
    private Long id;
    private Long abonnementId;
    private Long userId;
    private String username;
    private Integer score;
    private String comment;
    private LocalDateTime createdAt;
}