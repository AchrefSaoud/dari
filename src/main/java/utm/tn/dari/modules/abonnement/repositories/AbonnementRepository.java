package utm.tn.dari.modules.abonnement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utm.tn.dari.entities.Abonnement;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AbonnementRepository extends JpaRepository<Abonnement, Long> {
    List<Abonnement> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
