package utm.tn.dari.modules.annonce.services;


import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import utm.tn.dari.entities.User;
import utm.tn.dari.modules.annonce.Dtoes.AnnonceDTO;

import utm.tn.dari.security.services.UserService;

import java.util.List;

@Service
public class AsyncAnnonceService {

    private final SearchService searchService;
    private final UserService userService;
    private final MailingService mailingService;

    public AsyncAnnonceService(SearchService searchService, UserService userService, MailingService mailingService) {
        this.searchService = searchService;
        this.userService = userService;
        this.mailingService = mailingService;
    }

    @Async
    public void runUSearchQueryScanning(AnnonceDTO annonce) {
        List<Long> usersIds = searchService.getUsersFromUSearchQueryFiltered(annonce);
        System.out.println("Users to notify: " + usersIds);
        List<User> users = userService.getAllByIds(usersIds);
        sendNotificationEmailToUsers(users, annonce.getId());
    }

    private void sendNotificationEmailToUsers(List<User> users, Long announcementId) {
        for (User user : users) {
            try {
                mailingService.sendNotificationEmailForNewAnnouncementToUser(user.getUsername(), announcementId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}