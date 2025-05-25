package utm.tn.dari.modules.eventHandler.dtoes;

import lombok.Data;
import utm.tn.dari.entities.enums.EventType;

@Data
public class EventDto<T> {


    private EventType eventType;
    private T eventData;
}
