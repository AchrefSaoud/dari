package utm.tn.dari.modules.abonnement.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utm.tn.dari.entities.User;
import utm.tn.dari.modules.abonnement.services.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/{userId}/subscribe/{abonnementId}")
    public User subscribe(@PathVariable Long userId, @PathVariable Long abonnementId) {
        return userService.subscribeToAbonnement(userId, abonnementId);
    }
}
