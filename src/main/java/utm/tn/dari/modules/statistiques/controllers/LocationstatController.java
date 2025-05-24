package utm.tn.dari.modules.statistiques.controllers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import utm.tn.dari.modules.statistiques.service.StatistiqueLocation;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/statistiques/location")
@Tag(name = "Statistiques des Demandes de Location", description = "API pour consulter les statistiques liées aux demandes de location")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
public class LocationstatController {
    private final StatistiqueLocation statistiqueService;

    @GetMapping("/daily")
    @Operation(
            summary = "Nombre de demandes par jour",
            description = "Retourne le nombre total de demandes de location créées par jour dans une plage de dates"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques récupérées avec succès"),
            @ApiResponse(responseCode = "400", description = "Paramètres de dates invalides")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<LocalDate, Long>> getDemandeStatsByDay(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        log.info("REST request to get demande location stats by day between {} and {}", startDate, endDate);

        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(statistiqueService.getDemandeLocationCountPerDay(startDate, endDate));
    }

    @GetMapping("/by-status")
    @Operation(
            summary = "Nombre total de demandes par statut",
            description = "Retourne la répartition des demandes de location par statut (accepté, refusé, en attente)"
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Long>> getDemandeByStatus() {
        log.info("REST request to get demande location stats by status");
        return ResponseEntity.ok(statistiqueService.getDemandeCountByStatus());
    }

    @GetMapping("/monthly")
    @Operation(
            summary = "Nombre de demandes par mois",
            description = "Retourne le nombre total de demandes de location par mois"
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Long>> getDemandeStatsByMonth() {
        log.info("REST request to get demande location stats by month");
        return ResponseEntity.ok(statistiqueService.getDemandeCountPerMonth());
    }

    @GetMapping("/by-region")
    @Operation(
            summary = "Nombre de demandes par région",
            description = "Retourne la répartition géographique des demandes de location par région"
    )

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getDemandeAcceptanceRate() {
        log.info("REST request to get demande location acceptance rate stats");
        return ResponseEntity.ok(statistiqueService.getDemandeAcceptanceRate());
    }
}