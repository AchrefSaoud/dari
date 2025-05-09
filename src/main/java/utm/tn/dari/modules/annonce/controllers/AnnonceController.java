package utm.tn.dari.modules.annonce.controllers;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import utm.tn.dari.entities.enums.*;
import utm.tn.dari.modules.annonce.Dtoes.AnnonceDTO;
import utm.tn.dari.modules.annonce.Dtoes.AnnoncesPageDTO;
import utm.tn.dari.modules.annonce.exceptions.FileSavingException;
import utm.tn.dari.modules.annonce.exceptions.ObjectNotFoundException;
import utm.tn.dari.modules.annonce.exceptions.UnthorizedActionException;
import utm.tn.dari.modules.annonce.services.AnnonceService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/annonce")
public class AnnonceController {

    @Autowired
    private AnnonceService annonceService;
    final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(
            summary = "Poster une annonce",
            description = "Cette méthode permet de poster une annonce avec des fichiers joints. La taille maximale de chaque fichier est de 5MB.",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Annonce postée avec succès",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = AnnonceDTO.class)
                            )),
                    @ApiResponse(responseCode = "400", description = "Requête invalide : (Vérifier le format de fichier  (seuls images, vidéos et PDFs sont autorisés) ou données malformées)",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "403", description = "Action non autorisée : l'utilisateur n'a pas les droits pour effectuer cette action",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "404", description = "Objet introuvable : utilisateur ou autre ressource associée manquante",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> postAnnonce(
            @Parameter(description = "Données de l'annonce au format JSON",
                    example = "{ \"titre\": \"Annonce 1\", \"description\": \"Description de l'annonce\", \"prix\": 100.0, \"type\": \"VENTE\" }")
            @RequestPart("data") String data,

            @Parameter(description = "Fichiers joints (taille maximale de chaque fichier : 5MB, taille maximale totale : 500MB)",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestPart(value = "files", required = false) MultipartFile[] files) {
        try {


         List<MultipartFile> multipartFiles = new ArrayList<>();

           if(files != null && files.length != 0) {
               MultipartFile image  = files[0];
               if (image != null) {
                   if (!Objects.requireNonNull(image.getOriginalFilename()).split("\\.")[1].equals("jpg") && !image.getOriginalFilename().split("\\.")[1].equals("jpeg") && !image.getOriginalFilename().split("\\.")[1].equals("png")) {
                       return ResponseEntity.status(400).body("First file should be an image.Accepted types are jpg and jpeg and png.");
                   }
               }
               for (MultipartFile file : files) {
                   if (file.getSize() > MAX_FILE_SIZE) {
                       return ResponseEntity.status(400).body("File size exceeds the maximum allowed limit of 5MB: " + file.getOriginalFilename());
                   }
               }
               multipartFiles.addAll(List.of(files));

           }
            ObjectMapper objectMapper = new ObjectMapper();
            AnnonceDTO annonceDTO = objectMapper.readValue(data, AnnonceDTO.class);
            if(annonceDTO.getTypeBien() == null ){
                return ResponseEntity.status(400).body("Type du Bien ne peut etre null");

            }
            if(annonceDTO.getType() == null){
                return ResponseEntity.status(400).body("Type d'annonce ne peut etre null");
            }
            if(annonceDTO.getRooms() == null){
                return ResponseEntity.status(400).body("Le champ rooms ne peut etre null");
            }

            return ResponseEntity.ok(annonceService.postAnnonce(annonceDTO,multipartFiles));
        } catch (Exception e) {
            if (e instanceof FileSavingException) {
                return ResponseEntity.status(400).body(e.getMessage());
            }
            if (e instanceof ObjectNotFoundException) {
                return ResponseEntity.status(404).body(e.getMessage());
            } else if (e instanceof UnthorizedActionException) {
                return ResponseEntity.status(403).body(e.getMessage());
            } else {
                return ResponseEntity.status(400).body(e.getMessage());
            }
        }
    }



    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Récupérer une annonce par son ID",
            description = "Cette méthode permet de récupérer des annonces."
            ,responses = {
            @ApiResponse(responseCode = "200", description = "Annonces retournées avec succès"),
            @ApiResponse(responseCode = "400", description = "Requête invalide "),
            @ApiResponse(responseCode = "403", description = "Action non autorisée : l'utilisateur n'a pas les droits pour effectuer cette action"),
            @ApiResponse(responseCode = "404", description = "Objet introuvable : utilisateur ou autre ressource associée manquante")
    })
    @GetMapping
    @SecurityRequirement(name = "bearerAuth")

    public ResponseEntity<?> getQueriedAnnonces(
            @RequestParam(value = "query",required = false) String query,
            @RequestParam(value = "minPrix", required = false) Float minPrix,
            @RequestParam(value = "maxPrix", required = false) Float maxPrix,
            @RequestParam(value = "type", required = false) TypeAnnonce type,
            @RequestParam(value = "status", required = false) StatusAnnonce status,
            @RequestParam(value = "postername", required = false) String username,
            @RequestParam(value = "typeBien", required = false) TypeBien typeBien,
            @RequestParam(value = "rooms",required = false) Rooms rooms,
            @RequestParam(value = "leaseDuration",required = false) LeaseDuration leaseDuration,
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude,
            @RequestParam(value = "radius", required = false) Double radius,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size){

        try {
            Page<AnnonceDTO> annoncesPage = annonceService.getQueriedAnnonces(
                    query, type, status, username,minPrix,maxPrix,typeBien,rooms,leaseDuration, latitude, longitude, radius, page, size);

            AnnoncesPageDTO annoncesPageDTO = AnnoncesPageDTO.builder()
                    .annonces(annoncesPage.getContent())
                    .hasPrevious(annoncesPage.hasPrevious())
                    .hasNext(annoncesPage.hasNext())
                    .isFirst(annoncesPage.isFirst())
                    .isLast(annoncesPage.isLast())
                    .pageNumber(annoncesPage.getNumber())
                    .totalElements(annoncesPage.getTotalElements())
                    .totalPages(annoncesPage.getTotalPages())
                    .pageSize(annoncesPage.getSize())
                    .build();

            return ResponseEntity.ok(annoncesPageDTO);
        }catch (Exception e){
            if(e instanceof ObjectNotFoundException){
                return ResponseEntity.status(404).body(e.getMessage());
            }
            else if(e instanceof UnthorizedActionException){
                return ResponseEntity.status(403).body(e.getMessage());
            }
            else {
                return ResponseEntity.status(400).body(e.getMessage());
            }        }
    }



    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(
            summary = "Récupérer une annonce par son ID",
            description = "Cette méthode permet de récupérer une annonce en fonction de son ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Annonce retournée avec succès",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = AnnonceDTO.class)
                            )),
                    @ApiResponse(responseCode = "400", description = "Requête invalide",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "403", description = "Action non autorisée : l'utilisateur n'a pas les droits pour effectuer cette action",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "404", description = "Objet introuvable : utilisateur ou autre ressource associée manquante",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")

    public ResponseEntity<?> getAnnonceById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(annonceService.getAnnonceById(id));
        } catch (Exception e) {
            if (e instanceof ObjectNotFoundException) {
                return ResponseEntity.status(404).body(e.getMessage());
            } else if (e instanceof UnthorizedActionException) {
                return ResponseEntity.status(403).body(e.getMessage());
            } else {
                return ResponseEntity.status(400).body(e.getMessage());
            }
        }
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(
            summary = "Mettre à jour une annonce",
            description = "Cette méthode permet de mettre à jour une annonce avec des fichiers joints.",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Annonce mise à jour avec succès",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = AnnonceDTO.class)
                            )),
                    @ApiResponse(responseCode = "400", description = "Requête invalide : (Vérifier le format de fichier  (seuls images, vidéos et PDFs sont autorisés) ou données malformées)",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "403", description = "Action non autorisée : l'utilisateur n'a pas les droits pour effectuer cette action",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "404", description = "Objet introuvable : utilisateur ou autre ressource associée manquante",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    @SecurityRequirement(name = "bearerAuth")

    @PutMapping(value = "/{id}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateAnnonce(
            @PathVariable Long id,
            @RequestPart("data") String annonceDTOJson,
            @RequestPart(value = "image",required = false) MultipartFile image,
            @RequestPart(value = "files", required = false) MultipartFile[] files) {
        try {

            ObjectMapper objectMapper = new ObjectMapper();
            AnnonceDTO annonceDTO = objectMapper.readValue(annonceDTOJson, AnnonceDTO.class);
            List<MultipartFile> multipartFiles = new ArrayList<>();
            if(files != null && files.length != 0) {
                image = files[0];
                if (image != null) {
                    if (!Objects.requireNonNull(image.getOriginalFilename()).split("\\.")[1].equals("jpg") && !image.getOriginalFilename().split("\\.")[1].equals("jpeg") && !image.getOriginalFilename().split("\\.")[1].equals("png")) {
                        return ResponseEntity.status(400).body("Image types accepted are jpg and jpeg.");
                    }
                }
                for (MultipartFile file : files) {
                    if (file.getSize() > MAX_FILE_SIZE) {
                        return ResponseEntity.status(400).body("File size exceeds the maximum allowed limit of 5MB: " + file.getOriginalFilename());
                    }

                }
                multipartFiles.addAll(List.of(files));

            }

            return ResponseEntity.ok(annonceService.updateAnnonce(id, annonceDTO,multipartFiles));
        } catch (Exception e) {
            if (e instanceof FileSavingException) {
                return ResponseEntity.status(400).body(e.getMessage());
            }
            if (e instanceof ObjectNotFoundException) {
                return ResponseEntity.status(404).body(e.getMessage());
            } else if (e instanceof UnthorizedActionException) {
                return ResponseEntity.status(403).body(e.getMessage());
            } else {
                return ResponseEntity.status(400).body(e.getMessage());
            }
        }
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearerAuth")

    @Operation(
            summary = "Supprimer une annonce",
            description = "Cette méthode permet de supprimer une annonce en fonction de son ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Annonce supprimée avec succès",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "404", description = "Objet introuvable : annonce ou autre ressource associée manquante",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAnnonce(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(annonceService.deleteAnnonce(id));
        } catch (Exception e) {
            if (e instanceof ObjectNotFoundException) {
                return ResponseEntity.status(404).body(e.getMessage());
            } else if (e instanceof UnthorizedActionException) {
                return ResponseEntity.status(403).body(e.getMessage());
            } else {
                return ResponseEntity.status(400).body(e.getMessage());
            }
        }
    }
}
