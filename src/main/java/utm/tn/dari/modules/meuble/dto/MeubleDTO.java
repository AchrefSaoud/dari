package utm.tn.dari.modules.meuble.dto;

import lombok.Data;

@Data
public class MeubleDTO {
    private Long id;
    private String nom;
    private String description;
    private float prix;
    private String adresse;
    private String photoUrl;
    private Long vendeurId;
    private String vendeurUsername;
}