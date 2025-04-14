package utm.tn.dari.modules.bienImmobilier.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import utm.tn.dari.entities.BienImmobilier;

public interface BienImmRepository extends JpaRepository<BienImmobilier,Long> {
}
