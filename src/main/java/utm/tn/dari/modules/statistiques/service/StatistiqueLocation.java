package utm.tn.dari.modules.statistiques.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utm.tn.dari.modules.location.entities.DemandeLocation;
import utm.tn.dari.modules.location.repositories.DemandeLocationRepo;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatistiqueLocation {
    private final DemandeLocationRepo demandeLocationRepo;

    @Autowired
    public StatistiqueLocation(DemandeLocationRepo demandeLocationRepo) {
        this.demandeLocationRepo = demandeLocationRepo;
    }

    /**
     * Compte le nombre de demandes de location par jour dans une période donnée
     * @param startDate Date de début de la période
     * @param endDate Date de fin de la période
     * @return Map avec la date comme clé et le nombre de demandes comme valeur
     */
    public Map<LocalDate, Long> getDemandeLocationCountPerDay(LocalDate startDate, LocalDate endDate) {
        List<DemandeLocation> demandes = demandeLocationRepo.findAll();

        // Utilisation des streams pour plus de lisibilité et performance
        return demandes.stream()
                .map(demande -> demande.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate())
                .filter(date -> !date.isBefore(startDate) && !date.isAfter(endDate))
                .collect(Collectors.groupingBy(
                        date -> date,
                        TreeMap::new,
                        Collectors.counting()
                ));
    }

    /**
     * Compte le nombre de demandes par statut
     * @return Map avec le statut comme clé et le nombre de demandes comme valeur
     */
    public Map<String, Long> getDemandeCountByStatus() {
        List<DemandeLocation> demandes = demandeLocationRepo.findAll();

        return demandes.stream()
                .collect(Collectors.groupingBy(
                        demande -> demande.getStatus().toString(),
                        HashMap::new,
                        Collectors.counting()
                ));
    }

    /**
     * Compte le nombre de demandes par mois
     * @return Map avec le mois (format yyyy-MM) comme clé et le nombre de demandes comme valeur
     */
    public Map<String, Long> getDemandeCountPerMonth() {
        List<DemandeLocation> demandes = demandeLocationRepo.findAll();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        return demandes.stream()
                .collect(Collectors.groupingBy(
                        demande -> demande.getCreatedAt()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                                .withDayOfMonth(1)
                                .format(formatter),
                        TreeMap::new,
                        Collectors.counting()
                ));
    }

    /**
     * Récupère le nombre de demandes par région
     * @return Map avec la région comme clé et le nombre de demandes comme valeur
     */


    /**
     * Récupère le taux d'acceptation des demandes
     * @return Map avec des statistiques sur l'acceptation des demandes
     */
    public Map<String, Object> getDemandeAcceptanceRate() {
        List<DemandeLocation> demandes = demandeLocationRepo.findAll();
        long total = demandes.size();

        if (total == 0) {
            Map<String, Object> emptyStats = new HashMap<>();
            emptyStats.put("totalDemandes", 0L);
            emptyStats.put("acceptedCount", 0L);
            emptyStats.put("rejectedCount", 0L);
            emptyStats.put("pendingCount", 0L);
            emptyStats.put("acceptanceRate", 0.0);
            return emptyStats;
        }

        long accepted = demandes.stream()
                .filter(d -> "ACCEPTED".equals(d.getStatus().toString()))
                .count();

        long rejected = demandes.stream()
                .filter(d -> "REJECTED".equals(d.getStatus().toString()))
                .count();

        long pending = demandes.stream()
                .filter(d -> "PENDING".equals(d.getStatus().toString()))
                .count();

        double acceptanceRate = (double) accepted / total * 100.0;

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDemandes", total);
        stats.put("acceptedCount", accepted);
        stats.put("rejectedCount", rejected);
        stats.put("pendingCount", pending);
        stats.put("acceptanceRate", acceptanceRate);

        return stats;
    }
}