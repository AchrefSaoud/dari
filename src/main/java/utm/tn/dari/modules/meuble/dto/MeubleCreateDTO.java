package utm.tn.dari.modules.meuble.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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