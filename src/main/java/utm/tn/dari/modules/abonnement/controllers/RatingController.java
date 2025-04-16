package utm.tn.dari.modules.abonnement.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import utm.tn.dari.security.CurrentUser;
import utm.tn.dari.security.UserPrincipal;
import utm.tn.dari.modules.abonnement.dtos.RatingCreateDto;
import utm.tn.dari.modules.abonnement.dtos.RatingDto;
import utm.tn.dari.modules.abonnement.services.RatingService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class RatingController {
    private final RatingService ratingService;
    private Logger log;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    @Operation(summary = "Create a new rating")
    public ResponseEntity<?> createRating(@RequestBody @Valid RatingCreateDto dto,
                                          @CurrentUser UserPrincipal currentUser , @Parameter(hidden = true) @RequestParam(required = false) Map<String,String> allParams)
    {
        log.info("Creating rating for abonnement {} by user {}",
                dto.getAbonnementId(), currentUser.getId());
        try {
            RatingDto created = ratingService.createRating(dto, currentUser.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            log.error("Rating creation failed", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/abonnement/{abonnementId}")
    public ResponseEntity<List<RatingDto>> getRatingsForAbonnement(@PathVariable Long abonnementId) {
        List<RatingDto> ratings = ratingService.getRatingsForAbonnement(abonnementId);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/abonnement/{abonnementId}/average")
    public ResponseEntity<Map<String, Double>> getAverageRating(@PathVariable Long abonnementId) {
        Double average = ratingService.getAverageRating(abonnementId);
        return ResponseEntity.ok(Collections.singletonMap("averageRating", average));
    }
}