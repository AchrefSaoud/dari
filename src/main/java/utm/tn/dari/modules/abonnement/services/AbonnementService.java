package utm.tn.dari.modules.abonnement.services;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;
import utm.tn.dari.entities.Abonnement;
import utm.tn.dari.modules.abonnement.repositories.AbonnementRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AbonnementService {
    private final AbonnementRepository abonnementRepository;

    // ✅ Seul l'admin peut créer un abonnement
    @PreAuthorize("hasAuthority('ADMIN')")
    public Abonnement createAbonnement(Abonnement abonnement) {
        return abonnementRepository.save(abonnement);
    }

    // ✅ L'admin peut modifier un abonnement
    @PreAuthorize("hasAuthority('ADMIN')")
    public Abonnement updateAbonnement(Long id, Abonnement newAbonnement) {
        return abonnementRepository.findById(id).map(abonnement -> {
            abonnement.setNom(newAbonnement.getNom());
            abonnement.setDescription(newAbonnement.getDescription());
            abonnement.setPrix(newAbonnement.getPrix());
            abonnement.setType(newAbonnement.getType());
            return abonnementRepository.save(abonnement);
        }).orElseThrow(() -> new RuntimeException("Abonnement introuvable"));
    }

    // ✅ L'admin peut supprimer un abonnement
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteAbonnement(Long id) {
        abonnementRepository.deleteById(id);
    }

    // ✅ Tout le monde peut voir les abonnements
    public List<Abonnement> getAllAbonnements() {
        return abonnementRepository.findAll();
    }
}