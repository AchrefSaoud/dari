package utm.tn.dari.modules.authentication.dtos;

import lombok.Data;

@Data
public class UserLoginResponseDTO {
    private String username;
    private String jwt;
}