package utm.tn.dari.modules.annonce.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import utm.tn.dari.entities.Annonce;
import utm.tn.dari.entities.Recommendation;
import utm.tn.dari.entities.enums.*;
import utm.tn.dari.modules.annonce.Dtoes.AnnonceDTO;
import utm.tn.dari.modules.annonce.repositories.AnnonceRepository;
import utm.tn.dari.modules.annonce.repositories.RecommendationRepository;
import utm.tn.dari.modules.annonce.repositories.RecommendationSearchSpecification;
import utm.tn.dari.modules.annonce.services.AnnonceService;
import utm.tn.dari.modules.annonce.services.RecommendationService;
import utm.tn.dari.modules.user.dtos.UserDto;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendationServiceImpl implements RecommendationService {
    // Implement the methods defined in the RecommendationService interface here

    @Autowired
    private RecommendationRepository recommendationRepository;
    @Autowired
    private AnnonceRepository annonceRepository;

    @Override
    public List<UserDto> getUsersByAnnounceId(Long announceId) {
       try {
                List<UserDto> users = new ArrayList<>();
                Annonce annonce = annonceRepository.getReferenceById(announceId);
                List<utm.tn.dari.entities.Recommendation> recommendations = recommendationRepository.findByAnnonce(annonce);
                if (recommendations.isEmpty()) {
                    System.out.println("No recommendations found for the given announce ID");
                    return users;
                }
                for (utm.tn.dari.entities.Recommendation recommendation : recommendations) {
                    UserDto user = new UserDto();
                    user.setId(recommendation.getUser().getId());
                    user.setUsername(recommendation.getUser().getUsername());
                    users.add(user);
                }
                return users;
       }catch (Exception e){
              System.out.println("Error while getting users by announce id");
              e.printStackTrace();

       }
       return new ArrayList<>();
    }

    @Override
    public Page<AnnonceDTO> getAnnouncesByUserId(Long userId, String query, TypeAnnonce type, StatusAnnonce status,
                                                 String searchedUsername, Float minPrice, Float maxPrice,
                                                 TypeBien typeBien, Rooms rooms, LeaseDuration leaseDuration,
                                                 Double latitude, Double longitude, Double radius
                                                 , int pageNumber, int pageSize) {
        try {


           Pageable pageable = Pageable.ofSize(pageSize).withPage(pageNumber);
            System.out.println(" userId: " + userId);
            System.out.println(" query: " + query);
            System.out.println(" type: " + type);
            System.out.println(" status: " + status);
            System.out.println(" searchedUsername: " + searchedUsername);
            System.out.println(" minPrice: " + minPrice);
            System.out.println(" maxPrice: " + maxPrice);
            System.out.println(" typeBien: " + typeBien);
            System.out.println(" rooms: " + rooms);
            System.out.println(" leaseDuration: " + leaseDuration);
            System.out.println(" latitude: " + latitude);
            System.out.println(" longitude: " + longitude);
            System.out.println(" radius: " + radius);
            System.out.println(" pageNumber: " + pageNumber);
            System.out.println(" pageSize: " + pageSize);

          Page<Recommendation> recommendations =
                  recommendationRepository.findAll(RecommendationSearchSpecification.buildCombinedSpec
                                    (userId,leaseDuration,typeBien,rooms,query,minPrice,maxPrice,type,status,searchedUsername,latitude,longitude,radius),pageable);

          if(recommendations.isEmpty()) {
              System.out.println("No recommendations found for the given user ID");
              return null;
          }

            List<AnnonceDTO> annonceDTOs = new ArrayList<>();
            for (Recommendation recommendation : recommendations) {
                AnnonceDTO annonceDTO = new AnnonceDTO();
                Annonce annonce = recommendation.getAnnonce();
                annonceDTO.setId(annonce.getId());
                annonceDTO.setDescription(annonce.getDescription());
                annonceDTO.setPrix(annonce.getPrix());
                annonceDTO.setRooms(annonce.getRooms());
                annonceDTO.setTypeBien(annonce.getTypeBien());
                annonceDTO.setLeaseDuration(annonce.getLeaseDuration());
                annonceDTO.setStatus(annonce.getStatus());
                annonceDTO.setLatitude(annonce.getLatitude());
                annonceDTO.setLongitude(annonce.getLongitude());
                annonceDTO.setType(annonce.getType());
                annonceDTO.setUserId(annonce.getUser().getId());

                // Add other fields as necessary
                annonceDTOs.add(annonceDTO);
            }
            return new PageImpl<>(annonceDTOs, pageable,recommendations.getTotalElements());

        }catch (Exception e){
            System.out.println("Error while getting announces by user id");
            e.printStackTrace();
        }
        return new PageImpl<>(new ArrayList<>(), Pageable.ofSize(0), 0);
    }


}
