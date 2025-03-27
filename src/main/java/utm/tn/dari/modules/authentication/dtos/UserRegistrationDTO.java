package utm.tn.dari.modules.authentication.dtos;

import lombok.Data;
import utm.tn.dari.entities.enums.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
public class UserRegistrationDTO {
    private String username;
    private String password;
    private String telephone;
    private String nom;
    private Set<Role> roles;

    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        if (username == null || username.trim().isEmpty()) {
            errors.add("Username is required");
        }

        if (password == null || password.trim().isEmpty()) {
            errors.add("Password is required");
        } else if (password.length() < 8) {
            errors.add("Password must be at least 8 characters long");
        }

        return errors;
    }
}