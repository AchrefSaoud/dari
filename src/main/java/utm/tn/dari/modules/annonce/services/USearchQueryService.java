package utm.tn.dari.modules.annonce.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utm.tn.dari.entities.Annonce;
import utm.tn.dari.entities.USearchQuery;
import utm.tn.dari.entities.User;
import utm.tn.dari.modules.annonce.Dtoes.USearchQueryDTO;
import utm.tn.dari.modules.annonce.repositories.USearchQueryRepository;
import utm.tn.dari.security.services.UserService;

@Service
public class USearchQueryService {

    @Autowired
    USearchQueryRepository uSearchQueryRepository;

    @Autowired
    UserService userService;

    @Autowired
    AnnonceService  annonceService;

    public USearchQueryDTO saveUSearchQuery(USearchQueryDTO uSearchQueryDTO) throws Exception {

        // Check if the user exists
        if(uSearchQueryDTO.getUserId() == null) {
            throw new RuntimeException("User ID is required");
        }
        User user = userService.findUserById(uSearchQueryDTO.getUserId());
        if (user == null) {
            throw new RuntimeException("User not found");
        }



        USearchQuery uSearchQuery = new USearchQuery();
        uSearchQuery.setUser(user);
        uSearchQuery.setTitre(uSearchQueryDTO.getTitre());
        uSearchQuery.setDescription(uSearchQueryDTO.getDescription());
        uSearchQuery.setMinPrix(uSearchQueryDTO.getMinPrix());
        uSearchQuery.setMaxPrix(uSearchQueryDTO.getMaxPrix());
        uSearchQuery.setLatitude(uSearchQueryDTO.getLatitude());
        uSearchQuery.setLongitude(uSearchQueryDTO.getLongitude());
        uSearchQuery.setRadius(uSearchQueryDTO.getRadius());
        uSearchQuery.setType(uSearchQueryDTO.getType());
        uSearchQuery.setStatusAnnonce(uSearchQueryDTO.getStatusAnnonce());

        this.uSearchQueryRepository.save(uSearchQuery);



        return uSearchQueryDTO;
    }
}
