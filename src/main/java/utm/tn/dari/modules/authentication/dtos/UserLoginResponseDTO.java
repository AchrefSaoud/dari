package utm.tn.dari.modules.authentication.dtos;

import lombok.Data;

@Data
public class UserLoginResponseDTO {
    private String jwt;
    private Long userId;
    private String username;
}