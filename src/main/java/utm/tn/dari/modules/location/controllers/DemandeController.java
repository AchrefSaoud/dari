package utm.tn.dari.modules.location.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utm.tn.dari.entities.enums.DemandeLocationStatus;
import utm.tn.dari.modules.location.dtoes.DemandeLocationDTO;
import utm.tn.dari.modules.location.services.DemandeLocationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/demande")
public class DemandeController {

    @Autowired
    DemandeLocationService demandeLocationService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(
            summary = "Create a new location request",
            description = "This method allows users to create a new location request.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Location request created successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = DemandeLocationDTO.class)
                            )),
                    @ApiResponse(responseCode = "400", description = "Invalid request: malformed data or validation error",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "403", description = "Unauthorized action: user doesn't have required permissions",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    @PostMapping
    public ResponseEntity<?> createDemande(
            @Parameter(description = "Location request data", required = true,
                    schema = @Schema(implementation = DemandeLocationDTO.class))
            @RequestBody DemandeLocationDTO demandeLocationDTO) {
        try {
            demandeLocationDTO = demandeLocationService.saveDemandeLocation(demandeLocationDTO);
            return ResponseEntity.ok(demandeLocationDTO);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(
            summary = "Delete a location request",
            description = "This method allows users to delete a location request by its ID.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Location request deleted successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request: malformed data or validation error",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "403", description = "Unauthorized action: user doesn't have required permissions",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "404", description = "Location request not found",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDemande(
            @Parameter(description = "ID of the location request to delete", required = true)
            @PathVariable Long id) {
        try {
            demandeLocationService.deleteDemandeLocation(id);
            return ResponseEntity.ok("Demande deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }


    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(
            summary = "Update a location request status",
            description = "This method allows users to update the status of a location request.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Location request status updated successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = DemandeLocationDTO.class)
                            )),
                    @ApiResponse(responseCode = "400", description = "Invalid request: malformed data or validation error",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "403", description = "Unauthorized action: user doesn't have required permissions",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(responseCode = "404", description = "Location request not found",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateDemandeStatus(
            @Parameter(description = "ID of the location request to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "New status for the location request", required = true)
            @RequestParam String status) {
        try {
           /* DemandeLocationDTO updatedDemandeLocationDTO =
                    demandeLocationService.updateDemandeLocationStatus(id, status);*/
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
    @GetMapping("/{id}/status")
    @Operation(
            summary = "Get all location requests",
            description = "This method allows users to retrieve all location requests.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Location requests retrieved successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = DemandeLocationDTO.class)
                            )),
                    @ApiResponse(responseCode = "403", description = "Unauthorized action: user doesn't have required permissions",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    public ResponseEntity<?> getAllDemandes(@RequestParam Long id,@RequestParam(required = false) DemandeLocationStatus status) {
        try {
            return ResponseEntity.ok(demandeLocationService.getDemandeLocationByAnnonceIdAndStatus(id, status));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }


}