package utm.tn.dari.modules.eventHandler.services.impl;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import utm.tn.dari.UserInteraction;
import utm.tn.dari.config.KafkaEnableConfig;
import utm.tn.dari.entities.Annonce;
import utm.tn.dari.entities.User;
import utm.tn.dari.entities.enums.EventType;
import utm.tn.dari.entities.enums.UserInteractionType;
import utm.tn.dari.modules.abonnement.services.UserService;
import utm.tn.dari.modules.annonce.services.AnnonceService;
import utm.tn.dari.modules.eventHandler.dtoes.EventDto;
import utm.tn.dari.modules.eventHandler.dtoes.UserInteractionDTO;
import utm.tn.dari.modules.eventHandler.repository.UserInteractionRepository;
import utm.tn.dari.modules.eventHandler.services.EventHandlerService;

@Conditional(KafkaEnableConfig.class) // Load this service only if Kafka is enabled
@Service
public class UserInteractionServiceImpl implements EventHandlerService<UserInteractionDTO> {

    @Autowired
    UserService userService;

    @Autowired
    AnnonceService annonceService;

    @Autowired
    UserInteractionRepository userInteractionRepository;

    @Override
    @KafkaListener(topics = "client-events", groupId = "client-group",
            containerFactory = "eventHandlerKafkaListenerContainerFactory")
    public void saveEvent(EventDto<UserInteractionDTO> eventDto) throws Exception {
        try {

            if (!EventType.USER_INTERACTION.equals(eventDto.getEventType())) {
                return;
            }
            UserInteractionDTO userInteractionDTO = eventDto.getEventData();

            if (userInteractionDTO == null) {
                throw new IllegalArgumentException("UserInteractionDTO cannot be null");
            }

            User user = userService.getUserById(userInteractionDTO.getUserId());
            if (user == null) {
                throw new IllegalArgumentException("User not found with ID: " + userInteractionDTO.getUserId());
            }

            Annonce annonce = annonceService.getAnnonceObjById(userInteractionDTO.getAnnonceId());
            if (annonce == null) {
                throw new IllegalArgumentException("Annonce not found with ID: " + userInteractionDTO.getAnnonceId());
            }


            UserInteractionType interactionType = userInteractionDTO.getInteractionType();
            switch (interactionType) {
                case SEE:
                    userInteractionDTO.setInteractionScore(0.1f); // No score for view interaction
                    break;
                case VIEW:
                    userInteractionDTO.setInteractionScore(0.7f); // Score for like interaction
                    break;
                case CLICK:
                    userInteractionDTO.setInteractionScore(0.2f); // Score for click interaction
                    break;
                case REQUEST:
                    userInteractionDTO.setInteractionScore(0.99f); // Score for request interaction
                    break;
                default:
                    throw new IllegalArgumentException("Unknown interaction type: " + userInteractionDTO.getInteractionType());
            }
            UserInteraction userInteraction = new UserInteraction();
            userInteraction.setAnnonce(annonce);
            userInteraction.setUser(user);
            userInteraction.setInteractionType(userInteractionDTO.getInteractionType());
            userInteraction.setInteractionDate(userInteractionDTO.getInteractionDate());
            userInteraction.setInteractionScore(userInteractionDTO.getInteractionScore());

            userInteractionRepository.save(userInteraction);
            System.out.println("User interaction saved successfully: " + userInteractionDTO.getInteractionType() +
                    " for user ID: " + userInteractionDTO.getUserId() +
                    " on annonce ID: " + userInteractionDTO.getAnnonceId() + "type: " + userInteraction.getInteractionType());
        }catch (Exception e){
            throw new Exception("Error while processing event: " + e.getMessage(), e);
        }

    }
}
