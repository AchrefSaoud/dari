package utm.tn.dari.modules.eventHandler.services;

import org.apache.poi.ss.formula.functions.T;
import utm.tn.dari.modules.eventHandler.dtoes.EventDto;

public interface EventHandlerService<T> {

    void saveEvent(EventDto<T> eventDto) throws Exception;
}
