package utm.tn.dari.modules.user.dtos;

import java.util.Set;

import lombok.Data;
import utm.tn.dari.entities.enums.Role;
import utm.tn.dari.modules.abonnement.dtos.AbonnementDto;

@Data
public class UserResponseDto {
    private Long id;
    private String username;
    private String telephone;
    private String nom;
    private Set<Role> roles;
    private boolean active;
    private AbonnementDto abonnement;
    private String profilePicture;
}