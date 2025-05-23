package utm.tn.dari.modules.annonce.repositories;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import utm.tn.dari.entities.Annonce;
import utm.tn.dari.entities.Recommendation;
import utm.tn.dari.entities.enums.*;

public class RecommendationSearchSpecification {

    public static Specification<Recommendation> withUserId(Long userId) {
        return (root, query, cb) ->
                userId == null ? null : cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Recommendation> buildCombinedSpec(
            Long userId,
            LeaseDuration leaseDuration,
            TypeBien typeBien,
            Rooms rooms,
            String description,
            Float minPrice,
            Float maxPrice,
            TypeAnnonce type,
            StatusAnnonce status,
            String username,
            Double latitude,
            Double longitude,
            Double radius
    ) {
        return (root, query, cb) -> {
            // Prevent N+1 queries by fetching annonce only for entity results
            if (query.getResultType() == Recommendation.class) {
                root.fetch("annonce", JoinType.LEFT);
            }

            Join<Recommendation, Annonce> annonceJoin = root.join("annonce", JoinType.LEFT);
            Predicate predicate = cb.conjunction();

            if (userId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("user").get("id"), userId));
            }

            if (leaseDuration != null && leaseDuration != LeaseDuration.ANY) {
                predicate = cb.and(predicate, cb.equal(annonceJoin.get("leaseDuration"), leaseDuration));
            }

            if (typeBien != null && typeBien != TypeBien.ANY) {
                predicate = cb.and(predicate, cb.equal(annonceJoin.get("typeBien"), typeBien));
            }

            if (rooms != null && rooms != Rooms.ANY) {
                predicate = cb.and(predicate, cb.equal(annonceJoin.get("rooms"), rooms));
            }

            if (description != null && !description.isEmpty()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(annonceJoin.get("description")), "%" + description.toLowerCase() + "%"));
            }

            if (minPrice != null && maxPrice != null && (minPrice > 0 || maxPrice > 0)) {
                predicate = cb.and(predicate, cb.between(annonceJoin.get("prix"), minPrice, maxPrice));
            } else if (minPrice != null && minPrice > 0) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(annonceJoin.get("prix"), minPrice));
            } else if (maxPrice != null && maxPrice > 0) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(annonceJoin.get("prix"), maxPrice));
            }

            if (type != null) {
                predicate = cb.and(predicate, cb.equal(annonceJoin.get("type"), type));
            }

            if (status != null) {
                predicate = cb.and(predicate, cb.equal(annonceJoin.get("status"), status));
            }

            if (username != null && !username.isEmpty()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(annonceJoin.get("user").get("username")), "%" + username.toLowerCase() + "%"));
            }

            if (latitude != null && longitude != null && radius != null) {
                double degreeRadius = radius / 111.0; // 1 degree ~ 111km
                Predicate latBetween = cb.between(annonceJoin.get("latitude"), latitude - degreeRadius, latitude + degreeRadius);
                Predicate lonBetween = cb.between(annonceJoin.get("longitude"), longitude - degreeRadius, longitude + degreeRadius);
                predicate = cb.and(predicate, latBetween, lonBetween);
            }

            return predicate;
        };
    }
}
