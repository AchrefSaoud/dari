package utm.tn.dari.modules.abonnement.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import utm.tn.dari.entities.Abonnement;
import utm.tn.dari.entities.User;
import utm.tn.dari.modules.abonnement.repositories.AbonnementRepository;
import utm.tn.dari.modules.authentication.repositories.UserRepository;

@Service("UserService")
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AbonnementRepository abonnementRepository;

    public User subscribeToAbonnement(Long userId, Long abonnementId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        Abonnement abonnement = abonnementRepository.findById(abonnementId).orElseThrow(() -> new RuntimeException("Abonnement introuvable"));

        if (user.getAbonnement() != null) {
            throw new RuntimeException("L'utilisateur est déjà abonné !");
        }

        user.setAbonnement(abonnement);
        return userRepository.save(user);
    }
}

