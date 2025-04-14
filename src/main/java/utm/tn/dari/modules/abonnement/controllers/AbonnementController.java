package utm.tn.dari.modules.abonnement.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import utm.tn.dari.modules.abonnement.dtos.AbonnementCreateDto;
import utm.tn.dari.modules.abonnement.dtos.AbonnementDto;
import utm.tn.dari.modules.abonnement.mappers.AbonnementMapper;
import utm.tn.dari.modules.abonnement.services.AbonnementService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/abonnements")
@SecurityRequirement(name = "bearerAuth") 
@RequiredArgsConstructor
public class AbonnementController {

    @Autowired
    private final AbonnementService abonnementService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> create(@RequestBody AbonnementCreateDto dto) {
        try {
            AbonnementDto created = AbonnementMapper.toDto(
                abonnementService.createAbonnement(AbonnementMapper.toEntity(dto))
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Échec de la création de l'abonnement : " + e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody AbonnementCreateDto dto) {
        try {
            AbonnementDto updated = AbonnementMapper.toDto(
                abonnementService.updateAbonnement(id, AbonnementMapper.toEntity(dto))
            );
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Abonnement introuvable ou mise à jour échouée : " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            abonnementService.deleteAbonnement(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Échec de la suppression de l'abonnement : " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<AbonnementDto>> list() {
        List<AbonnementDto> abonnements = abonnementService.getAllAbonnements()
            .stream()
            .map(AbonnementMapper::toDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(abonnements);
    }
}