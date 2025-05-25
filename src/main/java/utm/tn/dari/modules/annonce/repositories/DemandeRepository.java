package utm.tn.dari.modules.annonce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utm.tn.dari.entities.Annonce;
import utm.tn.dari.entities.User;
import utm.tn.dari.modules.location.entities.Demande;

@Repository
public interface DemandeRepository  extends JpaRepository<Demande,Long> {

    public Demande deleteByAnnonceAndUser(Annonce annonce, User user);

}
