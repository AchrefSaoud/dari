package utm.tn.dari.modules.annonce.repositories;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.geo.Point;
import org.springframework.data.jpa.domain.Specification;
import utm.tn.dari.entities.Annonce;
import utm.tn.dari.entities.USearchQuery;
import utm.tn.dari.entities.enums.Rooms;
import utm.tn.dari.entities.enums.TypeBien;

public class UQuerySearchSpecification {
    public static Specification<USearchQuery> filterByRooms(Rooms rooms) {
        return (root, query, criteriaBuilder) -> {
            if (rooms == null) {
                return null; // No filter applied
            }
            return criteriaBuilder.equal(root.get("rooms"),rooms);
        };
    }


    public static Specification<USearchQuery> filterByTypeBien(TypeBien typeBien) {
        return (root, query, criteriaBuilder) -> {
            if (typeBien == null ) {
                return null; // No filter applied
            }
            return criteriaBuilder.equal(root.get("typeBien"),typeBien);
        };
    }


    public static Specification<USearchQuery> filterByGeolocalisation(Double latitude, Double longitude) {
        return (root, query, cb) -> {

            if (latitude == null || longitude == null ) {
                return null;
            }


            double degreeRadius = 1000;

            Expression<Double> entityLat = root.get("latitude");
            Expression<Double> entityLon = root.get("longitude");


            Predicate latInRange = cb.between(entityLat, latitude - degreeRadius, longitude + degreeRadius);
            Predicate lonInRange = cb.between(entityLon, latitude - degreeRadius, longitude + degreeRadius);


            return cb.and(latInRange, lonInRange);
        };
    }


    public static Specification<USearchQuery> filterByPriceRange(float prix) {
        return (root, query, criteriaBuilder) -> {
            if (prix <= 0) {
                return criteriaBuilder.conjunction(); // Always true condition
            }

            Path<Float> minPrixPath = root.get("minPrix");
            Path<Float> maxPrixPath = root.get("maxPrix");

            return criteriaBuilder.and(
                    criteriaBuilder.lessThanOrEqualTo(minPrixPath, prix),  // prix >= minPrix
                    criteriaBuilder.greaterThanOrEqualTo(maxPrixPath, prix) // prix <= maxPrix
            );
        };
    }



}
