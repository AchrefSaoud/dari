package utm.tn.dari.modules.annonce.events;


import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import utm.tn.dari.modules.annonce.Dtoes.AnnonceDTO;

@Getter
public class PriceChangedEvent extends ApplicationEvent {
    private final AnnonceDTO annonce;
    private final float oldPrice;
    private final float newPrice;
    
    public PriceChangedEvent(Object source, AnnonceDTO annonce, float oldPrice, float newPrice) {
        super(source);
        this.annonce = annonce;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
    }
}