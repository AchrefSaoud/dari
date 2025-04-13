package utm.tn.dari.modules.meuble.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utm.tn.dari.entities.Meuble;

import java.util.List;

@Repository
public interface MeubleRepository extends JpaRepository<Meuble, Long> {
    List<Meuble> findByVendeurId(Long vendeurId);
}