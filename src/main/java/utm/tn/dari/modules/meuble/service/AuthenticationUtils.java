package utm.tn.dari.modules.meuble.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import utm.tn.dari.entities.User;
import utm.tn.dari.modules.authentication.repositories.UserRepository;

@Component
public class AuthenticationUtils {

    private final UserRepository userRepository;

    public AuthenticationUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur authentifié introuvable"));
    }
}
