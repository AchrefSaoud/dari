package utm.tn.dari.modules.annonce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import utm.tn.dari.entities.USearchQuery;

@Repository
public interface USearchQueryRepository extends JpaRepository<USearchQuery, Long>, JpaSpecificationExecutor<USearchQuery> {
    // Custom query methods can be defined here if needed
}
