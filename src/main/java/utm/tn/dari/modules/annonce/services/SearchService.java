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
import utm.tn.dari.modules.annonce.repositories.UQuerySearchSpecification;
import utm.tn.dari.modules.annonce.repositories.USearchQueryRepository;

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




    public List<User> getUsersFromUSearchQueryFiltered(AnnonceDTO annonce){

        Specification<USearchQuery> uSearchQuerySpecification = Specification.where(null);

        if (annonce.getPrix() >= 0) {
            uSearchQuerySpecification = uSearchQuerySpecification.and(UQuerySearchSpecification.filterByPriceRange(annonce.getPrix()));
        }
        if (annonce.getLongitude() != null && annonce.getLatitude() != null) {

            uSearchQuerySpecification = uSearchQuerySpecification.and(UQuerySearchSpecification.filterByGeolocalisation(annonce.getLatitude(), annonce.getLongitude()));
        }


        List<USearchQuery> searchQueries = this.uSearchQueryRepository.findAll(uSearchQuerySpecification);
        return searchQueries.stream()
                .map(USearchQuery::getUser)
                .distinct()
                .toList();
    }



}
