package utm.tn.dari.modules.user.dtos;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDto {
    private String telephone;
    private String nom;
    
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
}