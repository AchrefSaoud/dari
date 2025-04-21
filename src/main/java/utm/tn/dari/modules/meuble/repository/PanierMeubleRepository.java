package utm.tn.dari.modules.meuble.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utm.tn.dari.entities.PanierMeuble;

import java.util.Optional;

@Repository
public interface PanierMeubleRepository extends JpaRepository<PanierMeuble, Long> {
    Optional<PanierMeuble> findByAcheteurId(Long acheteurId);
}
