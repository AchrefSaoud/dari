package utm.tn.dari.modules.annonce.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import utm.tn.dari.entities.User;
import utm.tn.dari.modules.annonce.Dtoes.AnnonceDTO;
import utm.tn.dari.modules.annonce.services.MailingService;
import utm.tn.dari.modules.annonce.services.SearchService;

import java.util.List;

@Service
public class PriceChangedEventHandler {

    @Autowired
    private MailingService mailingService;
    
    @Autowired
    private SearchService searchService;
    
    @EventListener
    public void handlePriceChangedEvent(PriceChangedEvent event) {
        AnnonceDTO annonceDTO = event.getAnnonce();
        float oldPrice = event.getOldPrice();
        float newPrice = event.getNewPrice();
        
        List<User> interestedUsers = searchService.getUsersFromUSearchQueryFiltered(annonceDTO);
        

        for (User user : interestedUsers) {
            try {
                mailingService.sendPriceChangeNotificationEmail(
                    user.getUsername(),
                    annonceDTO.getId(),
                    annonceDTO.getTitre(),
                    oldPrice,
                    newPrice
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}