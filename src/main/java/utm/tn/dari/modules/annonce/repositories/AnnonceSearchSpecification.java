package utm.tn.dari.modules.annonce.repositories;

import jakarta.persistence.criteria.*;
import org.springframework.data.geo.Point;
import org.springframework.data.jpa.domain.Specification;
import utm.tn.dari.entities.Annonce;
import utm.tn.dari.entities.enums.Rooms;
import utm.tn.dari.entities.enums.StatusAnnonce;
import utm.tn.dari.entities.enums.TypeAnnonce;
import utm.tn.dari.entities.enums.TypeBien;

public class AnnonceSearchSpecification {

    // Filter by title (case insensitive)
    public static Specification<Annonce> filterByTitle(String title) {
        return (root, query, criteriaBuilder) ->
                title == null || title.isEmpty() ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("titre")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Annonce> filterByTypeBien(TypeBien typeBien) {
        return (root, query, criteriaBuilder) ->
                typeBien == null  ? null : criteriaBuilder.equal(root.get("typeBien"),typeBien);
    }

    public static Specification<Annonce> filterByRooms(Rooms rooms) {
        return (root, query, criteriaBuilder) ->
                rooms == null  ? null : criteriaBuilder.equal(root.get("rooms"),rooms);
    }

    // Filter by description (case insensitive)
    public static Specification<Annonce> filterByDescription(String description) {
        return (root, query, criteriaBuilder) ->
                description == null || description.isEmpty() ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + description.toLowerCase() + "%");
    }

    // Filter by exact price
    public static Specification<Annonce> filterByPriceEqual(float price) {
        return (root, query, criteriaBuilder) ->
                price <= 0 ? null : criteriaBuilder.equal(root.get("prix"), price); // price <= 0 could also be null check
    }

    // Filter by price range (min, max)
    public static Specification<Annonce> filterByPriceRange(float minPrice, float maxPrice) {
        return (root, query, criteriaBuilder) ->
                (minPrice <= 0 && maxPrice <= 0) ? null : criteriaBuilder.between(root.get("prix"), minPrice, maxPrice);
    }

    // Filter by annonce type
    public static Specification<Annonce> filterByType(TypeAnnonce type) {
        return (root, query, criteriaBuilder) ->
                type == null ? null : criteriaBuilder.equal(root.get("type"), type);
    }

    // Filter by annonce status
    public static Specification<Annonce> filterByStatus(StatusAnnonce status) {
        return (root, query, criteriaBuilder) ->
                status == null ? null : criteriaBuilder.equal(root.get("status"), status);
    }

    // Filter by username (case insensitive)
    public static Specification<Annonce> filterByUsername(String username) {
        return (root, query, criteriaBuilder) ->
                username == null || username.isEmpty() ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("user").get("username")), "%" + username.toLowerCase() + "%");
    }

    public static Specification<Annonce> filterByGeolocalisation(Double latitude, Double longitude, Double radius) {
        return (root, query, cb) -> {
            if (latitude == null || longitude == null || radius == null) {
                return null;
            }

            // Directly use latitude and longitude fields in the entity
            Expression<Double> entityLat = root.get("latitude");
            Expression<Double> entityLon = root.get("longitude");

            // Convert radius from kilometers to approximate degree difference (1° ≈ 111 km)
            double degreeRadius = radius / 111.0;

            // Calculate bounding box using simple comparisons
            Predicate latBetween = cb.between(entityLat,
                    cb.literal(latitude - degreeRadius),
                    cb.literal(latitude + degreeRadius)
            );

            Predicate lonBetween = cb.between(entityLon,
                    cb.literal(longitude - degreeRadius),
                    cb.literal(longitude + degreeRadius)
            );

            // Combine both latitude and longitude range checks
            return cb.and(latBetween, lonBetween);
        };
    }


}
