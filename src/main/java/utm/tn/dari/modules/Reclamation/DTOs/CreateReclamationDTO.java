package utm.tn.dari.modules.Reclamation.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateReclamationDTO {

    private Long userID;

    @NotBlank
    @Size(max = 100)
    private String titre;

    @NotBlank
    @Size(max = 2000)
    private String contenu;

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }
}
