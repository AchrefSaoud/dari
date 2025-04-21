package utm.tn.dari.modules.abonnement.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping
    @Operation(summary = "Create a new rating")
    public ResponseEntity<?> createRating(@RequestBody @Valid RatingCreateDto dto) {
        try {
            RatingDto created = ratingService.createRating(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
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