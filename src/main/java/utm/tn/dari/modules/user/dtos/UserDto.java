package utm.tn.dari.modules.user.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import utm.tn.dari.entities.enums.Role;

import java.util.Set;

@Data
public class UserDto {
    private Long id;
    
    @NotBlank(message = "Username is required")
    private String username;
    
    private String telephone;
    private String nom;
    
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
    
    private Set<Role> roles;
}
