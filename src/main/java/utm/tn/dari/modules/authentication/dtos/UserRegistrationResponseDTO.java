package utm.tn.dari.modules.authentication.dtos;

import java.util.Set;

import lombok.Data;

@Data
public class UserRegistrationResponseDTO {
    private Long id;
    private String username;
    private String telephone;
    private String nom;
    private Set<String> roles; 
}