package utm.tn.dari.modules.annonce.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import utm.tn.dari.entities.Annonce;
import utm.tn.dari.entities.Recommendation;

import java.util.Arrays;
import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByAnnonce(Annonce annonce);

    @EntityGraph(attributePaths = {"annonce", "annonce.attachmentPaths", "user"})
    Page<Recommendation> findAll(Specification<Recommendation> recommendationSpecification, Pageable pageable);
}
