package utm.tn.dari.modules.abonnement.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import utm.tn.dari.entities.Abonnement;
import utm.tn.dari.modules.abonnement.repositories.AbonnementRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AbonnementService {
    private final AbonnementRepository abonnementRepository;

    public Abonnement createAbonnement(Abonnement abonnement) {
        return abonnementRepository.save(abonnement);
    }

    public Abonnement updateAbonnement(Long id, Abonnement newAbonnement) {
        return abonnementRepository.findById(id).map(abonnement -> {
            abonnement.setNom(newAbonnement.getNom());
            abonnement.setDescription(newAbonnement.getDescription());
            abonnement.setPrix(newAbonnement.getPrix());
            abonnement.setType(newAbonnement.getType());
            return abonnementRepository.save(abonnement);
        }).orElseThrow(() -> new RuntimeException("Abonnement introuvable"));
    }

    public void deleteAbonnement(Long id) {
        if (!abonnementRepository.existsById(id)) {
            throw new RuntimeException("Abonnement introuvable avec id: " + id);
        }
        abonnementRepository.deleteById(id);
    }

    public List<Abonnement> getAllAbonnements() {
        return abonnementRepository.findAll();
    }
}