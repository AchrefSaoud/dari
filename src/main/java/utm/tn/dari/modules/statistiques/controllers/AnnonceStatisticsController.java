package utm.tn.dari.modules.statistiques.controllers;



import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import utm.tn.dari.entities.enums.TypeAnnonce;
import utm.tn.dari.modules.statistiques.dto.statistics.*;
import utm.tn.dari.modules.annonce.exceptions.ObjectNotFoundException;
import utm.tn.dari.modules.annonce.exceptions.UnthorizedActionException;
import utm.tn.dari.modules.statistiques.service.AnnonceStatisticsService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/annonce/statistics")
@RequiredArgsConstructor
@Tag(name = "Statistiques des annonces", description = "API pour la gestion des statistiques des annonces")
public class AnnonceStatisticsController {
    private final AnnonceStatisticsService statisticsService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/general")
    @Operation(
            summary = "Obtenir les statistiques générales des annonces",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<GeneralStatisticsDTO> getGeneralStatistics() {
        return ResponseEntity.ok(statisticsService.getGeneralStatistics());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/by-type")
    @Operation(
            summary = "Obtenir les statistiques par type d'annonce",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<TypeStatisticsDTO> getStatisticsByType() {
        return ResponseEntity.ok(statisticsService.getStatisticsByType());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/by-status")
    @Operation(
            summary = "Obtenir les statistiques par statut d'annonce",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<StatusStatisticsDTO> getStatisticsByStatus() {
        return ResponseEntity.ok(statisticsService.getStatisticsByStatus());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/prices/{type}")
    @Operation(
            summary = "Obtenir les statistiques de prix par type d'annonce",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<PriceStatisticsDTO> getPriceStatisticsByType(
            @PathVariable TypeAnnonce type
    ) throws ObjectNotFoundException {
        return ResponseEntity.ok(statisticsService.getPriceStatisticsByType(type));
    }

    @GetMapping("/user")
    @Operation(
            summary = "Obtenir les statistiques de l'utilisateur courant",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<UserStatisticsDTO> getUserStatistics() throws UnthorizedActionException {
        return ResponseEntity.ok(statisticsService.getUserStatistics());
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<String> handleObjectNotFound(ObjectNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(UnthorizedActionException.class)
    public ResponseEntity<String> handleUnauthorized(UnthorizedActionException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ex.getMessage());
    }
}
