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
import utm.tn.dari.modules.annonce.elastic.documents.USearchQDoc;
import utm.tn.dari.modules.annonce.repositories.UQuerySearchSpecification;
import utm.tn.dari.modules.annonce.repositories.USearchQueryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private USearchQueryRepository uSearchQueryRepository;


    @Autowired
    private USearchQElasticSearchService uSearchQElasticSearchService;


    public List<Long> getUsersFromUSearchQueryFiltered(AnnonceDTO annonce) {
        try {
            Specification<USearchQuery> uSearchQuerySpecification = Specification.where(null);

            if (annonce.getPrix() >= 0) {
                uSearchQuerySpecification = uSearchQuerySpecification.and(
                        UQuerySearchSpecification.filterByPriceRange(annonce.getPrix()));
            }



            List<USearchQuery> searchQueries = this.uSearchQueryRepository.findAll(uSearchQuerySpecification);

            System.out.println(searchQueries.size());
            Set<Long> uSearchQDocIds = this.uSearchQElasticSearchService
                    .getAllUSearchQDocsByQuery(annonce.getTitre() + " " + annonce.getDescription()).stream().map(USearchQDoc::getId).collect(Collectors.toSet());

            System.out.println(uSearchQDocIds.size());
            return getFilteredUsers(searchQueries,uSearchQDocIds,annonce);

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public List<Long> getFilteredUsers(List<USearchQuery> searchQueries, Set<Long> uSearchQDocIds, AnnonceDTO annonce) {
        try {
            // Step 1: Filter queries by matching document IDs
            List<USearchQuery> filteredByDocIds = searchQueries.stream()
                    .filter(uSearchQuery -> uSearchQDocIds.contains(uSearchQuery.getId()))
                    .toList();

            System.out.println("FILTER 1");

            // Step 2: Further filter based on geolocation and radius logic
            List<USearchQuery> filteredByLocationAndRadius = filteredByDocIds.stream()
                    .filter(uSearchQuery -> {
                        System.out.println(uSearchQuery.getLatitude() + " " + uSearchQuery.getLongitude() + " " + uSearchQuery.getRadius());
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
                            System.out.println("LATITUDE "+annonce.getLatitude());
                            System.out.println("Distance " + distance);
                            System.out.println("Radius " + uSearchQuery.getRadius());
                            return distance <= uSearchQuery.getRadius(); // ✅ correct logic
                        }
                        return true; // skip filter if any value is missing
                    })
                    .toList();

            // Step 3: Extract users and use a Set to ensure distinct results

            // Convert the Set back to a List
            // Collect to a Set to handle uniqueness

            System.out.println("FILTER 2");

            List<Long> usersIds = filteredByLocationAndRadius.stream()
                    .map(uSearchQuery -> uSearchQuery.getUser().getId()).distinct().toList();
            System.out.println("FILTER 3");

            return usersIds;

        }catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();

    }
}
