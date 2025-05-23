package utm.tn.dari.modules.annonce.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utm.tn.dari.modules.annonce.Dtoes.AnnonceDTO;
import utm.tn.dari.modules.annonce.events.NotificationsToBeSentEvent;
import utm.tn.dari.modules.annonce.services.AnnonceService;
import utm.tn.dari.modules.annonce.services.KafkaListenerService;
import utm.tn.dari.modules.annonce.services.MailingService;
import utm.tn.dari.modules.user.dtos.UserDto;

import java.util.ArrayList;
import java.util.List;

@Service
public class KafkaListenerServiceImpl implements KafkaListenerService {
    @Autowired
    private AnnonceService annonceService;
    @Autowired
    private MailingService mailingService;

    @Autowired
    private RecommendationServiceImpl recommendationService;
    @Override
    public void listenToTopic(NotificationsToBeSentEvent notificationsToBeSentEvent) {
        try {
            List<AnnonceDTO> simAnnonces = annonceService.getAllAnnoncesByIds(notificationsToBeSentEvent.getSimAnnouncesIds());
            if(simAnnonces.isEmpty()){
                System.out.println("No annonce found");
                return;
            }
            List<UserDto> users = new ArrayList<>();
            for (AnnonceDTO annonce : simAnnonces) {
                List<UserDto> usersByAnnounceId = recommendationService.getUsersByAnnounceId(annonce.getId());
                if(usersByAnnounceId.isEmpty()){
                    System.out.println("No users found");
                    return;
                }
                users.addAll(usersByAnnounceId);
            }
            mailingService.sendNotificationEmailForNewAnnouncementToUsers(users.stream().map(UserDto::getUsername).toList(), notificationsToBeSentEvent.getAnnounceId());


        }catch (Exception e){
            System.out.println("Error while sending notification email");
            e.printStackTrace();
        }
    }
}
