package utm.tn.dari.modules.visite.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;
import utm.tn.dari.entities.Annonce;
import utm.tn.dari.entities.User;
import utm.tn.dari.entities.Visite;

import utm.tn.dari.modules.annonce.repositories.AnnonceRepository;
import utm.tn.dari.modules.annonce.services.MailingService;
import utm.tn.dari.modules.authentication.repositories.UserRepository;

import utm.tn.dari.modules.visite.dtos.VisiteDTO;
import utm.tn.dari.modules.visite.repo.VisiteRepo;
import utm.tn.dari.security.services.UserService;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VisiteService {
    private final UserService userService;
    private final VisiteRepo visiteRepository;
    private final AnnonceRepository annonceRepository;
    private final MailingServ mailingService;
    public VisiteService(UserService userService, VisiteRepo visiteRepository,
                         AnnonceRepository annonceRepository, MailingServ mailingService) {
        this.userService = userService;
        this.visiteRepository = visiteRepository;
        this.annonceRepository = annonceRepository;
        this.mailingService = mailingService;
    }

    public List<VisiteDTO> getAvailableSlots(Long annonceId) {
        return visiteRepository.findAvailableSlots(annonceId, LocalDateTime.now()).stream()
                .map(VisiteDTO::new)
                .collect(Collectors.toList());
    }

    public Visite createSlot(Long userId, Long annonceId,
                             LocalDateTime startTime, LocalDateTime endTime) {


        User owner = userService.findUserById(userId);
        Annonce annonce = annonceRepository.findById(annonceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Annonce not found"));


        Visite visite = new Visite();
        visite.setOwner(owner);
        visite.setAnnonce(annonce);
        visite.setStartTime(startTime);
        visite.setEndTime(endTime);

        return visiteRepository.save(visite);
    }

    public Visite bookSlot(Long userId, Long visiteId) {
        Visite visite = visiteRepository.findById(visiteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Slot not found"));

        if (!visite.isAvailable()) {
            throw new ResponseStatusException(HttpStatus.IM_USED,"Slot not available");
        }

        User client = userService.findUserById(userId);
        visite.setClient(client);
        mailingService.sendNotificationEmailForNewRDVToUser(visite);
        return visiteRepository.save(visite);
    }

    public List<VisiteDTO> getMyCreatedSlots(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        return visiteRepository.findMyCreatedSlots(userId, now )
                .stream()
                .map(VisiteDTO::new)
                .collect(Collectors.toList());
    }

    public List<VisiteDTO> getMyBookedSlots(Long userId) {
        return visiteRepository.findMyBookedSlots(userId, LocalDateTime.now()).stream()
                .map(VisiteDTO::new)
                .collect(Collectors.toList());
    }

    public void deleteVisite(Long visiteId, Long userId) {
        Visite visite = visiteRepository.findById(visiteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Visite not found"));

        // Check if user is owner or admin
        if (!visite.getOwner().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can only delete visites you've created");
        }

        // Prevent deletion if visite is already booked
        if (visite.getClient() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot delete visite that has been booked. Cancel it first.");
        }

        visiteRepository.delete(visite);

        // Optional: Send notification
    }

    public Visite cancelVisite(Long visiteId, Long userId) {
        Visite visite = visiteRepository.findById(visiteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Visite not found"));

        // Verify the user is the client who booked this visite
        if (visite.getClient() == null || !visite.getClient().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You can only cancel visites you've booked");
        }

        // Verify it's not too late to cancel (e.g., at least 24 hours before)
        if (LocalDateTime.now().isAfter(visite.getStartTime().minusHours(24))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cancellation must be done at least 24 hours before the visite");
        }

        // Keep track of cancellation
        visite.setClient(null);

        Visite updatedVisite = visiteRepository.save(visite);



        return updatedVisite;
    }
}