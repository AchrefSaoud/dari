package utm.tn.dari.modules.annonce.events;

import lombok.Data;

import java.util.List;

@Data
public class NotificationsToBeSentEvent {

    private List<Long> simAnnouncesIds ;
    private Long announceId;
}
