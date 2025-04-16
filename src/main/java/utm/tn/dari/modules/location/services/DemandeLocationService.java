package utm.tn.dari.modules.location.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import utm.tn.dari.entities.Annonce;
import utm.tn.dari.entities.User;
import utm.tn.dari.entities.enums.DemandeLocationStatus;
import utm.tn.dari.modules.annonce.exceptions.ObjectNotFoundException;
import utm.tn.dari.modules.annonce.exceptions.UnthorizedActionException;
import utm.tn.dari.modules.annonce.services.AnnonceService;
import utm.tn.dari.modules.location.CannotBeAcceptedException;
import utm.tn.dari.modules.location.dtoes.DemandeLocationDTO;
import utm.tn.dari.modules.location.entities.DemandeLocation;
import utm.tn.dari.modules.location.repositories.DemandeLocationRepo;
import utm.tn.dari.security.services.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DemandeLocationService {

    @Autowired
    private DemandeLocationRepo demandeLocationRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private AnnonceService annonceService;

    public DemandeLocationDTO saveDemandeLocation(DemandeLocationDTO demandeLocationDTO) throws Exception{
        try {
            User user = userService.findUserById(demandeLocationDTO.getUserId());
            if(user == null) {
                throw new ObjectNotFoundException("User not found");
            }
            Annonce annonce = annonceService.getAnnonceObjById(demandeLocationDTO.getAnnonceId());
            if(annonce == null) {
                throw new ObjectNotFoundException("Annonce not found");
            }


            if(getDemandeLocationByAnnonceIdAndStatus(annonce.getId(), DemandeLocationStatus.PAIMENT_EN_COURS) != null
            || getDemandeLocationByAnnonceIdAndStatus(annonce.getId(), DemandeLocationStatus.ACCEPTEE) != null) {
                throw new CannotBeAcceptedException("La demande de location ne peut pas etre traitée pour ce moment.");
            }
            DemandeLocation demandeLocation = new DemandeLocation();
            demandeLocation.setDateDebutLocation(demandeLocationDTO.getDateDebut());
            demandeLocation.setDateFinLocation(demandeLocationDTO.getDateFin());
            demandeLocation.setUser(user);
            demandeLocation.setAnnonce(annonce);
            demandeLocation.setStatus(DemandeLocationStatus.EN_ATTENTE);
            demandeLocation.setMessage(demandeLocationDTO.getMessage());
            demandeLocation.setCreatedAt(LocalDateTime.now());
            demandeLocation.setDateDebutLocation(demandeLocationDTO.getDateDebut());
            demandeLocation.setDateFinLocation(demandeLocationDTO.getDateFin());

           demandeLocation =  this.demandeLocationRepo.save(demandeLocation);

           return DemandeLocationDTO.builder()
                     .id(demandeLocation.getId())
                     .userId(user.getId())
                     .annonceId(annonce.getId())
                     .status(demandeLocation.getStatus())
                     .dateDemande(demandeLocation.getCreatedAt())
                     .dateDebut(demandeLocation.getDateDebutLocation())
                     .dateFin(demandeLocation.getDateFinLocation())
                     .message(demandeLocation.getMessage())
                     .lettreEng(demandeLocation.getLettreEng())
                     .justifcatifPaiementdeCaution(demandeLocation.getJustifcatifPaiementdeCaution())
                     .fichesDePaies(demandeLocation.getFichesDePaies())
                     .build();





        }catch (Exception e){
            if(e instanceof ObjectNotFoundException) {
                throw e;
            } else if(e instanceof CannotBeAcceptedException) {
                throw e;
            } else {
                throw new RuntimeException("An error occurred while saving the demande location", e);
            }
        }
    }
   public  DemandeLocationDTO updateDemandeLocation(Long id, DemandeLocationStatus status) throws Exception {
        try {
            if(status == null) {
                throw new IllegalArgumentException("Status cannot be null");
            }
            if(id == null) {
                throw new IllegalArgumentException("Demande location ID cannot be null");
            }

            org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if(user == null) {
                throw new UnthorizedActionException("User not found");
            }
            User userInfo = userService.findByUsername(user.getUsername());
            DemandeLocation demandeLocation = this.demandeLocationRepo.findById(id).orElseThrow(() -> new ObjectNotFoundException("Demande location not found"));

            if(userInfo == null) {
                throw new UnthorizedActionException("User not found");
            }

            if(!userInfo.getId().equals(demandeLocation.getAnnonce().getUser().getId())){
                throw new UnthorizedActionException("You are not authorized to update this demande location");
            }

            if(status == DemandeLocationStatus.ACCEPTEE) {
                if(demandeLocation.getStatus() != DemandeLocationStatus.EN_ATTENTE ) {
                    throw new CannotBeAcceptedException("La demande de location ne peut pas etre acceptée.");
                }
            }
            else  {
                if(demandeLocation.getStatus() != DemandeLocationStatus.PAIMENT_EN_COURS) {
                    throw new CannotBeAcceptedException("La demande de location ne peut pas etre rejetée.");
                }
            }

            demandeLocation.setStatus(status);
            demandeLocation = this.demandeLocationRepo.save(demandeLocation);


            demandeLocation.setStatus(status);
            demandeLocation =   this.demandeLocationRepo.save(demandeLocation);

            return DemandeLocationDTO.builder()
                    .dateDemande(demandeLocation.getCreatedAt())
                    .dateFin(demandeLocation.getDateFinLocation())
                    .dateDebut(demandeLocation.getDateDebutLocation())
                    .annonceId(demandeLocation.getAnnonce().getId())
                    .userId(demandeLocation.getUser().getId())
                    .fichesDePaies(demandeLocation.getFichesDePaies())
                    .lettreEng(demandeLocation.getLettreEng())
                    .justifcatifPaiementdeCaution(demandeLocation.getJustifcatifPaiementdeCaution())
                    .status(demandeLocation.getStatus())
                    .message(demandeLocation.getMessage())
                    .build();
        }catch (Exception e){
            if(e instanceof ObjectNotFoundException) {
                throw e;
            } else {
                throw new RuntimeException("An error occurred while updating the demande location status", e);
            }
        }
    }

    public void deleteDemandeLocation(Long id) throws Exception {
        try {
            if(id == null) {
                throw new IllegalArgumentException("Demande location ID cannot be null");
            }
            DemandeLocation demandeLocation = this.demandeLocationRepo.findById(id).orElseThrow(() -> new ObjectNotFoundException("Demande location not found"));
            this.demandeLocationRepo.delete(demandeLocation);
        }catch (Exception e){
            if(e instanceof ObjectNotFoundException) {
                throw e;
            } else {
                throw new RuntimeException("An error occurred while deleting the demande location", e);
            }
        }
    }
    public DemandeLocation getDemandeLocationByAnnonceIdAndStatus(Long annonceId, DemandeLocationStatus status) throws Exception {
        try {
            Annonce annonce = annonceService.getAnnonceObjById(annonceId);
            if(annonce == null) {
                throw new ObjectNotFoundException("Annonce not found");
            }
            for(DemandeLocation demandeLocation : annonce.getDemandeLocations()) {
                if(demandeLocation.getStatus().equals(status)) {
                    return demandeLocation;
                }
            }

        }catch (Exception e){
            if(e instanceof ObjectNotFoundException) {
                throw e;
            } else {
                throw new RuntimeException("An error occurred while retrieving the demande location", e);
            }
        }
        return null;
    }
}
