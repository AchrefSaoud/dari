package utm.tn.dari.modules.abonnement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import utm.tn.dari.entities.Rating;
import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    // Ajoutez cette méthode
    boolean existsByAbonnementIdAndUserId(Long abonnementId, Long userId);

    List<Rating> findByAbonnementId(Long abonnementId);

    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.abonnement.id = :abonnementId")
    Double findAverageRatingByAbonnementId(@Param("abonnementId") Long abonnementId);
}