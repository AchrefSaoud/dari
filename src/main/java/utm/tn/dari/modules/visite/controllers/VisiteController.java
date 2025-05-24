package utm.tn.dari.modules.visite.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import utm.tn.dari.entities.Visite;
import utm.tn.dari.modules.visite.dtos.CreateSlotRequest;
import utm.tn.dari.modules.visite.dtos.RequestId;
import utm.tn.dari.modules.visite.dtos.VisiteDTO;
import utm.tn.dari.modules.visite.services.VisiteService;

import java.util.List;

@RestController
@RequestMapping("/api/visites")
public class VisiteController {
    private final VisiteService visiteService;

    public VisiteController(VisiteService visiteService) {
        this.visiteService = visiteService;
    }

    // Get available slots for an annonce
    @GetMapping("/available/{annonceId}")
    public List<VisiteDTO> getAvailableSlots(@PathVariable Long annonceId) {
        return visiteService.getAvailableSlots(annonceId);
    }

    // Book a slot
    @PostMapping("/book/{visiteId}")
    public Visite bookSlot(
            @PathVariable Long visiteId,
            @RequestBody RequestId requestId
            ) {
        return visiteService.bookSlot( requestId.getUserId(),visiteId);
    }

    // Create a new availability slot
    @PostMapping("/create")
    public Visite createSlot(
            @RequestBody CreateSlotRequest request
            ) {


        return visiteService.createSlot(
                request.getUserId(),
                request.getAnnonceId(),
                request.getStartTime(),
                request.getEndTime()
        );
    }

    // Get slots I've created
    @GetMapping("/my-created/{userId}")
    public List<VisiteDTO> getMyCreatedSlots(
            @PathVariable Long userId) {
        return visiteService.getMyCreatedSlots(userId);
    }

    // Get slots I've booked

    @GetMapping("/my-booked/{userId}")
    public List<VisiteDTO> getMyBookedSlots(
            @PathVariable long userId) {
        return visiteService.getMyBookedSlots(userId);
    }
    @DeleteMapping("/{visiteId}/{userId}")
    public ResponseEntity<?> deleteVisite(
            @PathVariable Long visiteId,
            @PathVariable Long userId
            ) {
        visiteService.deleteVisite(visiteId, userId);
        return ResponseEntity.ok().build();
    }
    @PutMapping("/cancel-as-client/{visiteId}/{userId}")
    public ResponseEntity<Visite> cancelVisite(
            @PathVariable Long visiteId,
            @PathVariable Long userId) {
        Visite cancelledVisite = visiteService.cancelVisite(visiteId, userId);
        return ResponseEntity.ok(cancelledVisite);
    }
}