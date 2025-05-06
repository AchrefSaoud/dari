package utm.tn.dari.modules.statistiques.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import utm.tn.dari.modules.statistiques.dto.AbonnementStatsDto;
import utm.tn.dari.modules.statistiques.dto.MonthlyStatsDto;
import utm.tn.dari.modules.statistiques.service.StatistiqueService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistiques")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Slf4j
public class StatistiqueController {

    private final StatistiqueService statistiqueService;

    /**
     * Récupère les statistiques générales des abonnements
     * @return Les statistiques globales
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AbonnementStatsDto> getAbonnementStatistics() {
        log.info("REST request to get abonnement statistics");
        return ResponseEntity.ok(statistiqueService.getAbonnementStats());
    }

    /**
     * Récupère les statistiques mensuelles pour une année spécifique
     * @param year L'année pour laquelle récupérer les statistiques (par défaut, l'année courante)
     * @return Liste des statistiques mensuelles
     */
    @GetMapping("/monthly")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<MonthlyStatsDto>> getMonthlyStatistics(
            @RequestParam(required = false) Integer year) {

        // Si l'année n'est pas fournie, utiliser l'année courante
        int yearToUse = year != null ? year : LocalDate.now().getYear();

        log.info("REST request to get monthly statistics for year: {}", yearToUse);
        return ResponseEntity.ok(statistiqueService.getMonthlyStats(yearToUse));
    }

    /**
     * Récupère le type d'abonnement le plus populaire (le plus souscrit)
     * @return Le type d'abonnement le plus populaire
     */
    @GetMapping("/most-popular-type")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getMostPopularAbonnementType() {
        log.info("REST request to get most popular abonnement type");
        AbonnementStatsDto stats = statistiqueService.getAbonnementStats();
        Map<String, Object> response = new HashMap<>();
        response.put("mostPopularType", stats.getMostPopularType());
        return ResponseEntity.ok(response);
    }

    /**
     * Récupère le montant total généré par les abonnements
     * @return Le montant total des revenus générés
     */
    @GetMapping("/total-revenue")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getTotalRevenue() {
        log.info("REST request to get total revenue from abonnements");
        AbonnementStatsDto stats = statistiqueService.getAbonnementStats();
        Map<String, Object> response = new HashMap<>();
        response.put("totalRevenue", stats.getTotalRevenue());
        return ResponseEntity.ok(response);
    }
}