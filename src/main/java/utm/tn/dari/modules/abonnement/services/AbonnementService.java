package utm.tn.dari.modules.abonnement.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import utm.tn.dari.entities.Abonnement;
import utm.tn.dari.entities.User;
import utm.tn.dari.modules.abonnement.mappers.AbonnementMapper;
import utm.tn.dari.modules.abonnement.repositories.AbonnementRepository;
import utm.tn.dari.modules.authentication.repositories.UserRepository;
import utm.tn.dari.modules.user.exceptions.ResourceNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional 
@Slf4j
public class AbonnementService {
    private final AbonnementRepository abonnementRepository;
    private final UserRepository userRepository; 
private final SmsService smsService;
private final PdfService pdfService;
    public Abonnement createAbonnement(Abonnement abonnement) {


            Abonnement saved = abonnementRepository.save(abonnement);
        String pdfPath = pdfService.generateAbonnementPdf(saved);
        System.out.println("PDF généré à : " + pdfPath);


        // Ici tu envoies le SMS
            String message = String.format("Merci pour votre abonnement de type %s à %.2f DT.",
                    saved.getType(), saved.getPrix());

            // Numéro du client à personnaliser
            String userPhoneNumber = "+216xxxxxxxx";
            smsService.sendSms(userPhoneNumber, message);

            return saved;
        }

        public Abonnement getAbonnementById(Long id) {
        return abonnementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Abonnement not found with id: " + id));
    }
    public Abonnement updateAbonnement(Long id, Abonnement partialAbonnement) {
        log.info("Attempting to update abonnement with id: {}", id);
        return abonnementRepository.findById(id).map(existingAbonnement -> {
            if (partialAbonnement.getNom() != null) {
                existingAbonnement.setNom(partialAbonnement.getNom());
            }
            if (partialAbonnement.getDescription() != null) {
                existingAbonnement.setDescription(partialAbonnement.getDescription());
            }
            if (partialAbonnement.getPrix() != null) {
                existingAbonnement.setPrix(partialAbonnement.getPrix());
            }
            if (partialAbonnement.getType() != null) {
                existingAbonnement.setType(partialAbonnement.getType());
            }
            return abonnementRepository.save(existingAbonnement);
        }).orElseThrow(() -> new RuntimeException("Abonnement introuvable"));
    }

    @Transactional
    public void deleteAbonnement(Long id) {
        Abonnement abonnement = abonnementRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Abonnement not found"));
        
        List<User> users = userRepository.findByAbonnementId(id);
        
        users.forEach(user -> {
            user.setAbonnement(null);
            userRepository.save(user);
        });
        
        abonnementRepository.delete(abonnement);
    }

    public boolean existsById(Long id) {
        return abonnementRepository.existsById(id);
    }
    public List<Abonnement> getAllAbonnements() {
        return abonnementRepository.findAll();
    }
}