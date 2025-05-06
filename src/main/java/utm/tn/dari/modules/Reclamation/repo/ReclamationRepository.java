package utm.tn.dari.modules.Reclamation.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import utm.tn.dari.entities.Reclamation;

import java.util.List;

public interface ReclamationRepository extends JpaRepository<Reclamation, Long> {
    @Query("SELECT r FROM Reclamation r WHERE r.createdBy.id = :userId")
    List<Reclamation> findByUserId(@Param("userId") Long userId);

    @Query("SELECT r FROM Reclamation r WHERE r.createdBy.id = :userId OR :isAdmin = true")
    List<Reclamation> findAllForUser(@Param("userId") Long userId, @Param("isAdmin") boolean isAdmin);
}