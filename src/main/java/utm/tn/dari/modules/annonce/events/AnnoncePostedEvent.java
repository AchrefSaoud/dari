package utm.tn.dari.modules.annonce.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import utm.tn.dari.modules.annonce.Dtoes.AnnonceDTO;

@Getter
public class AnnoncePostedEvent extends ApplicationEvent {

    private final AnnonceDTO annonce;
    public AnnoncePostedEvent(Object source, AnnonceDTO annonce) {
        super(source);
        this.annonce = annonce;
    }


}
