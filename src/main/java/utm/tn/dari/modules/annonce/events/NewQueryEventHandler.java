package utm.tn.dari.modules.annonce.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import utm.tn.dari.modules.annonce.services.USearchQueryService;

@Service
public class NewQueryEventHandler {

    @Autowired
    USearchQueryService uSearchQueryService;

    @EventListener
    public void handleNewQueryEvent(NewQueryEvent newQueryEvent){
        try {
            uSearchQueryService.saveUSearchQuery(newQueryEvent.getUSearchQueryDTO());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
