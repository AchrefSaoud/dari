package utm.tn.dari.modules.abonnement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utm.tn.dari.entities.Abonnement;
@Repository
public interface AbonnementRepository extends JpaRepository<Abonnement, Long> {
}