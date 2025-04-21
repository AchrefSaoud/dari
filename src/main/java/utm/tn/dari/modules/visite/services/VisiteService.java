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

    public VisiteService(UserService userService, VisiteRepo visiteRepository,
                         AnnonceRepository annonceRepository) {
        this.userService = userService;
        this.visiteRepository = visiteRepository;
        this.annonceRepository = annonceRepository;
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
}