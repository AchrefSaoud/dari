package utm.tn.dari.modules.statistiques.service;

import org.springframework.stereotype.Service;
import utm.tn.dari.modules.annonce.repositories.AnnonceRepository;
import utm.tn.dari.modules.statistiques.dto.AnnonceStatistiquesDTO;
import utm.tn.dari.entities.enums.StatusAnnonce; // Add this import

@Service
public class AnnonceServiceStat {

    private final AnnonceRepository annonceRepository;

    public AnnonceServiceStat(AnnonceRepository annonceRepository) {
        this.annonceRepository = annonceRepository;
    }

    public AnnonceStatistiquesDTO getStatistiques() {
        // Get total count of announcements
        long total = annonceRepository.count();

        // Pass enum values directly without toString()
        long actives = annonceRepository.countByStatus(StatusAnnonce.ACTIVE);
        long inactives = annonceRepository.countByStatus(StatusAnnonce.INACTIVE);

        // Create and populate statistics DTO
        AnnonceStatistiquesDTO stats = new AnnonceStatistiquesDTO();
        stats.setTotalAnnonces(total);
        stats.setAnnoncesActives(actives);
        stats.setAnnoncesInactives(inactives);
        stats.setAnnoncesAujourdHui(0);
        stats.setAnnoncesCetteSemaine(0);

        return stats;
    }
}