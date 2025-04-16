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
public class PostedAnnouncementEventHandler {

    @Autowired
    MailingService mailingService;

    @Autowired
    SearchService searchService;

    @EventListener
    public void handleAnnoncePostedEvent(AnnoncePostedEvent event) {

        AnnonceDTO annonceDTO = event.getAnnonce();
        runUSearchQueryScanning(annonceDTO);


    }

    public void runUSearchQueryScanning(AnnonceDTO annonce) {


        List<User> users = this.searchService.getUsersFromUSearchQueryFiltered(annonce);
        System.out.println("Users to notify: " + users);
        sendNotificationEmailToUsers(users, annonce.getId());
    }
    public void sendNotificationEmailToUsers(List<User> users, Long announcementId) {
        for (User user : users) {
            try {
                mailingService.sendNotificationEmailForNewAnnouncementToUser(user.getUsername(),announcementId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
