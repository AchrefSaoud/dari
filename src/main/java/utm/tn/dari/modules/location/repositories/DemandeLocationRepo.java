package utm.tn.dari.modules.location.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utm.tn.dari.modules.location.entities.Demande;


@Repository
public interface DemandeLocationRepo extends JpaRepository<Demande, Long> {
    // Custom query methods can be defined here if needed
}
