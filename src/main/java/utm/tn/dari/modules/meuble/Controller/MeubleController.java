package utm.tn.dari.modules.meuble.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import utm.tn.dari.modules.meuble.dto.MeubleCreateDTO;
import utm.tn.dari.modules.meuble.dto.MeubleDTO;
import utm.tn.dari.modules.meuble.service.FileStorageService;
import utm.tn.dari.modules.meuble.service.IMeubleService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/meubles")
@RequiredArgsConstructor
@Tag(name = "Meuble Management", description = "Endpoints pour la gestion des meubles")
public class MeubleController {

    private final IMeubleService meubleService;
    private final FileStorageService fileStorageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Ajouter un nouveau meuble avec photo optionnelle")
    @ApiResponse(responseCode = "200", description = "Meuble créé avec succès",
            content = @Content(schema = @Schema(implementation = MeubleDTO.class)))
    public ResponseEntity<MeubleDTO> ajouter(
            @Parameter(description = "Nom du meuble", required = true)
            @RequestParam("nom") String nom,

            @Parameter(description = "Description du meuble", required = true)
            @RequestParam("description") String description,

            @Parameter(description = "Prix du meuble", required = true)
            @RequestParam("prix") float prix,

            @Parameter(description = "Adresse où se trouve le meuble", required = true)
            @RequestParam("adresse") String adresse,

            @Parameter(description = "Fichier image optionnel")
            @RequestParam(value = "file", required = false) MultipartFile file) {

        try {
            MeubleCreateDTO dto = new MeubleCreateDTO();
            dto.setNom(nom);
            dto.setDescription(description);
            dto.setPrix(prix);
            dto.setAdresse(adresse);

            if (file != null && !file.isEmpty()) {
                String photoUrl = fileStorageService.storeFile(file);
                dto.setPhotoUrl(photoUrl);
            }

            return ResponseEntity.ok(meubleService.ajouterMeuble(dto));
        } catch (IOException e) {
            throw new RuntimeException("Échec de téléchargement du fichier: " + e.getMessage(), e);
        }
    }

    @GetMapping
    @Operation(summary = "Obtenir tous les meubles")
    public ResponseEntity<List<MeubleDTO>> listerTous() {
        return ResponseEntity.ok(meubleService.listerTousLesMeubles());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un meuble par ID")
    public ResponseEntity<Void> supprimer(
            @Parameter(description = "ID du meuble à supprimer", required = true)
            @PathVariable Long id) {
        meubleService.supprimerMeuble(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un meuble par ID")
    public ResponseEntity<MeubleDTO> getById(
            @Parameter(description = "ID du meuble à récupérer", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(meubleService.getById(id));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Mettre à jour un meuble")
    public ResponseEntity<MeubleDTO> modifier(
            @Parameter(description = "ID du meuble à mettre à jour", required = true)
            @PathVariable Long id,

            @Parameter(description = "Nom du meuble", required = true)
            @RequestParam("nom") String nom,

            @Parameter(description = "Description du meuble", required = true)
            @RequestParam("description") String description,

            @Parameter(description = "Prix du meuble", required = true)
            @RequestParam("prix") float prix,

            @Parameter(description = "Adresse où se trouve le meuble", required = true)
            @RequestParam("adresse") String adresse,

            @Parameter(description = "Fichier image optionnel")
            @RequestParam(value = "file", required = false) MultipartFile file) {

        try {
            MeubleCreateDTO dto = new MeubleCreateDTO();
            dto.setNom(nom);
            dto.setDescription(description);
            dto.setPrix(prix);
            dto.setAdresse(adresse);

            if (file != null && !file.isEmpty()) {
                String photoUrl = fileStorageService.storeFile(file);
                dto.setPhotoUrl(photoUrl);
            }

            return ResponseEntity.ok(meubleService.modifierMeuble(id, dto));
        } catch (IOException e) {
            throw new RuntimeException("Échec de téléchargement du fichier: " + e.getMessage(), e);
        }
    }
}