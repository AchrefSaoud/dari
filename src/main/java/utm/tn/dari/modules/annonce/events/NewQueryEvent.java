package utm.tn.dari.modules.annonce.events;

import lombok.Data;
import org.springframework.context.ApplicationEvent;
import utm.tn.dari.entities.Annonce;
import utm.tn.dari.modules.annonce.Dtoes.USearchQueryDTO;


@Data

public class NewQueryEvent  extends ApplicationEvent {


    private USearchQueryDTO uSearchQueryDTO;
    public NewQueryEvent(Object source, USearchQueryDTO uSearchQueryDTO) {
        super(source);
        this.uSearchQueryDTO = uSearchQueryDTO;

    }
}
