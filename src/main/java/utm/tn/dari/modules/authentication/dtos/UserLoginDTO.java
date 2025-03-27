package utm.tn.dari.modules.authentication.dtos;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class UserLoginDTO {
    private String username;
    private String password;

    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        if (username == null || username.trim().isEmpty()) {
            errors.add("Username is required");
        }
        if (password == null || password.trim().isEmpty()) {
            errors.add("Password is required");
        }

        return errors;
    }
}