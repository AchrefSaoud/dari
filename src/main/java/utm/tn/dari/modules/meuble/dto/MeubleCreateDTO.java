package utm.tn.dari.modules.meuble.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor



public class MeubleCreateDTO {
    private String nom;
    private String description;
    private float prix;
    private String adresse;
    private String photoUrl;

}