package utm.tn.dari.modules.Reclamation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import utm.tn.dari.entities.Reclamation;
import utm.tn.dari.modules.Reclamation.DTOs.CreateReclamationDTO;
import utm.tn.dari.modules.Reclamation.DTOs.ReclamationDTO;
import utm.tn.dari.modules.Reclamation.DTOs.ReclamationDetailsDTO;
import utm.tn.dari.modules.Reclamation.service.ReclamationService;

import java.util.List;

@RestController
@RequestMapping("/api/reclamations")
@RequiredArgsConstructor
public class ReclamationController {
    private final ReclamationService reclamationService;

    @PostMapping
    public ResponseEntity<ReclamationDTO> createReclamation(
            @Valid @RequestBody CreateReclamationDTO dto
            ) {
        Reclamation reclamation = reclamationService.createReclamation(dto, dto.getUserID());
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(reclamation));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<ReclamationDTO>> getUserReclamations(
            @PathVariable Long userId
            ) {

        boolean isAdmin =true;

        List<ReclamationDTO> reclamations = reclamationService
                .getUserReclamations(userId, isAdmin);

        return ResponseEntity.ok(reclamations);
    }

    @GetMapping("/{id}/{userId}")
    public ResponseEntity<ReclamationDetailsDTO> getReclamationDetails(
            @PathVariable Long id,
            @PathVariable Long userId) {
        boolean isAdmin = true;

        ReclamationDetailsDTO dto = reclamationService
                .getReclamationDetails(id, userId, isAdmin);

        return ResponseEntity.ok(dto);
    }

    private ReclamationDTO convertToDTO(Reclamation reclamation) {
        ReclamationDTO dto = new ReclamationDTO();
        dto.setId(reclamation.getId());
        dto.setTitre(reclamation.getTitre());
        dto.setStatus(reclamation.getStatus());
        dto.setCreatedAt(reclamation.getCreatedAt());


        return dto;
    }
}