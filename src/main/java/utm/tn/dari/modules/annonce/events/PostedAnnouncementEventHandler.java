package utm.tn.dari.modules.annonce.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import utm.tn.dari.entities.User;
import utm.tn.dari.modules.annonce.Dtoes.AnnonceDTO;
import utm.tn.dari.modules.annonce.services.AsyncAnnonceService;
import utm.tn.dari.modules.annonce.services.MailingService;
import utm.tn.dari.modules.annonce.services.SearchService;
import utm.tn.dari.security.services.UserService;

import java.util.List;

@Service
public class PostedAnnouncementEventHandler {

    @Autowired
    MailingService mailingService;

    @Autowired
    SearchService searchService;

    @Autowired
    UserService userService;

    @Autowired
    AsyncAnnonceService asyncAnnonceService;

    @EventListener
    public void handleAnnoncePostedEvent(AnnoncePostedEvent event) {

        try {
            AnnonceDTO annonceDTO = event.getAnnonce();
            asyncAnnonceService.runUSearchQueryScanning(annonceDTO);
        }catch (Exception e){
            e.printStackTrace();
        }



    }

}
