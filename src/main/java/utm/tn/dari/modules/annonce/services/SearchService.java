package utm.tn.dari.modules.annonce.services;

import jakarta.persistence.criteria.Expression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import utm.tn.dari.entities.USearchQuery;
import utm.tn.dari.entities.User;
import utm.tn.dari.modules.annonce.Dtoes.AnnonceDTO;
import utm.tn.dari.modules.annonce.Utils.Haversine;
import utm.tn.dari.modules.annonce.repositories.UQuerySearchSpecification;
import utm.tn.dari.modules.annonce.repositories.USearchQueryRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchService {

    @Autowired
    private USearchQueryRepository uSearchQueryRepository;



    public List<USearchQuery> getAveragePriceByType(String type) {

        return this.uSearchQueryRepository.findAll().stream()
                .filter(searchRequest -> searchRequest.getType().name().equals(type)).toList();
    }

    public List<USearchQuery> getAveragePriceByStatus(String status) {

        return this.uSearchQueryRepository.findAll().stream()
                .filter(searchRequest -> searchRequest.getStatusAnnonce().name().equals(status)).toList();
    }
    public List<USearchQuery> getAveragePriceByDescription(String description) {

        return this.uSearchQueryRepository.findAll().stream()
                .filter(searchRequest -> searchRequest.getDescription().equals(description)).toList();
    }



    public List<User> getUsersFromUSearchQueryFiltered(AnnonceDTO annonce) {
        try {
            Specification<USearchQuery> uSearchQuerySpecification = Specification.where(null);

            if (annonce.getPrix() >= 0) {
                uSearchQuerySpecification = uSearchQuerySpecification.and(
                        UQuerySearchSpecification.filterByPriceRange(annonce.getPrix()));
            }


            List<USearchQuery> searchQueries = this.uSearchQueryRepository.findAll(uSearchQuerySpecification);

            return searchQueries.stream()
                    .filter(uSearchQuery -> {
                        if (
                                annonce.getLatitude() != null &&
                                        annonce.getLongitude() != null &&
                                        uSearchQuery.getLatitude() != null &&
                                        uSearchQuery.getLongitude() != null &&
                                        uSearchQuery.getRadius() != null
                        ) {
                            double distance = Haversine.distance(
                                    annonce.getLatitude(), annonce.getLongitude(),
                                    uSearchQuery.getLatitude(), uSearchQuery.getLongitude()
                            );
                            System.out.println("Distance " + distance);
                            System.out.println("Radius " + uSearchQuery.getRadius());
                            return distance <= uSearchQuery.getRadius(); // ✅ correct logic
                        }
                        return true; // skip filter if any value is missing
                    })
                    .map(USearchQuery::getUser)
                    .distinct()
                    .toList();

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

}
