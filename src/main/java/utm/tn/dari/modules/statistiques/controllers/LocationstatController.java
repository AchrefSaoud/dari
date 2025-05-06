package utm.tn.dari.modules.statistiques.controllers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import utm.tn.dari.modules.statistiques.service.StatistiqueLocation;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@Tag(name = "Statistiques des Demandes de Location")
public class LocationstatController {
    @Autowired
    private StatistiqueLocation statistiqueService;

    @GetMapping("/demande-location/daily")
    @Operation(summary = "Nombre de demandes par jour", description = "Retourne le nombre total de demandes de location créées par jour dans une plage de dates")
    public Map<LocalDate, Long> getDemandeStatsByDay(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return statistiqueService.getDemandeLocationCountPerDay(startDate, endDate);
    }
    @GetMapping("/demande-location/by-status")
    @Operation(summary = "Nombre total de demandes par statut")
    public Map<String, Long> getDemandeByStatus() {
        return statistiqueService.getDemandeCountByStatus();
    }

    @GetMapping("/demande-location/monthly")
    @Operation(summary = "Nombre de demandes par mois", description = "Retourne le nombre total de demandes de location par mois")
    public Map<String, Long> getDemandeStatsByMonth() {
        return statistiqueService.getDemandeCountPerMonth();
    }

}
