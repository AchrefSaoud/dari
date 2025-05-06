package utm.tn.dari.modules.statistiques.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utm.tn.dari.modules.location.entities.DemandeLocation;
import utm.tn.dari.modules.location.repositories.DemandeLocationRepo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class StatistiqueLocation {
    @Autowired
    private DemandeLocationRepo demandeLocationRepo;

    public Map<LocalDate, Long> getDemandeLocationCountPerDay(LocalDate startDate, LocalDate endDate) {
        List<DemandeLocation> demandes = demandeLocationRepo.findAll();

        Map<LocalDate, Long> result = new TreeMap<>();

        for (DemandeLocation demande : demandes) {
            LocalDate date = demande.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate();
            if ((date.isEqual(startDate) || date.isAfter(startDate)) && (date.isEqual(endDate) || date.isBefore(endDate))) {
                result.put(date, result.getOrDefault(date, 0L) + 1);
            }
        }

        return result;
    }
    public Map<String, Long> getDemandeCountByStatus() {
        List<DemandeLocation> demandes = demandeLocationRepo.findAll();
        Map<String, Long> statusCount = new HashMap<>();

        for (DemandeLocation d : demandes) {
            String status = d.getStatus().toString();
            statusCount.put(status, statusCount.getOrDefault(status, 0L) + 1);
        }
        return statusCount;
    }

    public Map<String, Long> getDemandeCountPerMonth() {
        List<DemandeLocation> demandes = demandeLocationRepo.findAll();
        Map<String, Long> result = new TreeMap<>();

        for (DemandeLocation d : demandes) {
            String key = d.getCreatedAt()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .withDayOfMonth(1)
                    .toString()
                    .substring(0, 7); // Format: yyyy-MM
            result.put(key, result.getOrDefault(key, 0L) + 1);
        }
        return result;
    }


}
