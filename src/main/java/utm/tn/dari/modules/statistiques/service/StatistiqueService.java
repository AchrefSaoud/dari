// src/main/java/utm/tn/dari/modules/statistiques/services/StatistiqueService.java
package utm.tn.dari.modules.statistiques.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import utm.tn.dari.modules.abonnement.repositories.AbonnementRepository;
import utm.tn.dari.modules.statistiques.dto.AbonnementTypeStatDTO;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatistiqueService {

    private final AbonnementRepository abonnementRepository;

    public List<AbonnementTypeStatDTO> getNombreAbonnementsParType() {
        return abonnementRepository.findAll().stream()
                .collect(Collectors.groupingBy(a -> a.getType(), Collectors.counting()))
                .entrySet().stream()
                .map(e -> new AbonnementTypeStatDTO(e.getKey().name(), e.getValue()))

                .collect(Collectors.toList());
    }
}
