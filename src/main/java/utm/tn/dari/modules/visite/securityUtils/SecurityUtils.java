package utm.tn.dari.modules.visite.securityUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import utm.tn.dari.entities.User;
import utm.tn.dari.security.services.UserService;

@Component
public class SecurityUtils {

    @Autowired
    private UserService userService;

    public User getCurrentUser() {
        org.springframework.security.core.userdetails.User userDetails =
                (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal();

        return userService.findByUsername(userDetails.getUsername());
    }
}