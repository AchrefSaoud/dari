package utm.tn.dari.modules.abonnement.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import utm.tn.dari.entities.Abonnement;
import utm.tn.dari.entities.enums.TypeAbonnement;
import utm.tn.dari.modules.abonnement.dtos.AbonnementCreateDto;
import utm.tn.dari.modules.abonnement.dtos.AbonnementDto;
import utm.tn.dari.modules.abonnement.dtos.RatingDto;
import utm.tn.dari.modules.abonnement.mappers.AbonnementMapper;
import utm.tn.dari.modules.abonnement.services.AbonnementService;
import org.springframework.web.multipart.MultipartFile;
import utm.tn.dari.modules.abonnement.services.RatingService;
import utm.tn.dari.modules.user.exceptions.ResourceNotFoundException;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/abonnements")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Slf4j
public class AbonnementController {
    private final AbonnementService abonnementService;
    private final RatingService ratingService;
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
    @GetMapping("/{id}")
    public ResponseEntity<?> getAbonnementById(@PathVariable Long id) {
        try {
            Abonnement abonnement = abonnementService.getAbonnementById(id);
            AbonnementDto dto = AbonnementMapper.toDto(abonnement);


            return ResponseEntity.ok(dto);
        } catch (ResourceNotFoundException e) {
            log.error("Abonnement not found with id: {}", id, e);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Abonnement introuvable avec l'ID: " + id);
        }
    }
    @PostMapping(value = "/create-with-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createWithFile(
            @RequestParam("nom") String nom,
            @RequestParam("description") String description,
            @RequestParam("prix") BigDecimal prix,
            @RequestParam("type") TypeAbonnement type,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        try {
            // Create the AbonnementCreateDto from the form parameters
            AbonnementCreateDto dto = new AbonnementCreateDto();
            dto.setNom(nom);
            dto.setDescription(description);
            dto.setPrix(prix);
            dto.setType(type);

            // Create the abonnement
            Abonnement abonnement = abonnementService.createAbonnement(AbonnementMapper.toEntity(dto));
            AbonnementDto created = AbonnementMapper.toDto(abonnement);

            // If a file was uploaded, save it
            if (file != null && !file.isEmpty()) {
                String fileName = saveFile(file, abonnement.getId());
                // If you want to store the file path in the future, you can add that functionality here
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            log.error("Error creating abonnement with file", e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Échec de la création de l'abonnement : " + e.getMessage());
        }
    }
    private String saveFile(MultipartFile file, Long abonnementId) throws Exception {
        // Create a custom filename
        String fileName = "abonnement_" + abonnementId + "_" +
                System.currentTimeMillis() + "_" +
                file.getOriginalFilename().replaceAll("\\s+", "_");

        // Save file to a directory
        String uploadDir = "./uploads/abonnements";

        // Create directory if it doesn't exist
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Save the file
        Path path = Paths.get(uploadDir + File.separator + fileName);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "abonnementId", required = true) Long abonnementId) {
        try {
            // Check if file is empty
            if (file.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                                "success", false,
                                "message", "Veuillez sélectionner un fichier à télécharger"
                        ));
            }

            // Create a custom filename
            String fileName = "abonnement_" + abonnementId + "_" +
                    System.currentTimeMillis() + "_" +
                    file.getOriginalFilename().replaceAll("\\s+", "_");

            // Save file to a directory
            String uploadDir = "./uploads/abonnements";

            // Create directory if it doesn't exist
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Save the file
            Path path = Paths.get(uploadDir + File.separator + fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            // Return JSON response with file details
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("fileName", fileName);
            response.put("filePath", path.toString());
            response.put("fileSize", file.getSize());
            response.put("contentType", file.getContentType());
            response.put("abonnementId", abonnementId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error uploading file", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Échec du téléchargement du fichier : " + e.getMessage()
                    ));
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
            log.error("Error updating abonnement", e);
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
            log.error("Error deleting abonnement", e);
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
    @GetMapping("/{id}/ratings")
    public ResponseEntity<?> getAbonnementRatings(@PathVariable Long id) {
        try {
            List<RatingDto> ratings = ratingService.getRatingsForAbonnement(id);
            return ResponseEntity.ok(ratings);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/average-rating")
    public ResponseEntity<?> getAbonnementAverageRating(@PathVariable Long id) {
        try {
            Double average = ratingService.getAverageRating(id);
            return ResponseEntity.ok(Collections.singletonMap("averageRating", average));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}