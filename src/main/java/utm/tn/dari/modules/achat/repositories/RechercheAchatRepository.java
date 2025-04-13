package utm.tn.dari.modules.achat.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utm.tn.dari.entities.RechercheAchat;

import java.util.List;
@Repository
public interface RechercheAchatRepository extends JpaRepository<RechercheAchat, Long> {
    List<RechercheAchat> findByUserId(Long userId);
}