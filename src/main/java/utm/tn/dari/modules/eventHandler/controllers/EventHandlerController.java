package utm.tn.dari.modules.eventHandler.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utm.tn.dari.modules.eventHandler.dtoes.EventDto;
import utm.tn.dari.modules.eventHandler.dtoes.UserInteractionDTO;
import utm.tn.dari.modules.eventHandler.services.EventHandlerService;

import java.util.UUID;

@RestController
@RequestMapping("/api/event")
public class EventHandlerController {

    @Value("${kafka.enabled:false}")
    private boolean kafkaEnabled;

    @Autowired(required = false)
    KafkaTemplate<String, EventDto<UserInteractionDTO>> kafkaTemplate;



    @PostMapping
    public void saveEvent(@RequestBody EventDto<UserInteractionDTO> userInteractionEventDto) throws Exception {
        try {

            if(!kafkaEnabled){

                return;
            }

            kafkaTemplate.send("client-events", UUID.randomUUID().toString(), userInteractionEventDto);
        }catch (Exception e){
            throw new Exception("Error while saving event: " + e.getMessage(), e);
        }
    }

    @PostMapping("/batch")
    public void saveBatchEvents(@RequestBody EventDto<UserInteractionDTO>[] userInteractionEventDtos) throws Exception {
        try {
            if (!kafkaEnabled) {
                return;
            }
            for (EventDto<UserInteractionDTO> eventDto : userInteractionEventDtos) {
                kafkaTemplate.send("client-events", UUID.randomUUID().toString(), eventDto);
            }
        } catch (Exception e) {
            throw new Exception("Error while saving batch events: " + e.getMessage(), e);
        }
    }
}
