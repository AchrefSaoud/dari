package utm.tn.dari.modules.annonce.services;

import org.springframework.data.domain.Page;
import utm.tn.dari.entities.enums.*;
import utm.tn.dari.modules.annonce.Dtoes.AnnonceDTO;
import utm.tn.dari.modules.user.dtos.UserDto;

import java.util.List;

public interface RecommendationService {
     List<UserDto> getUsersByAnnounceId(Long announceId);
    Page<AnnonceDTO> getAnnouncesByUserId(Long userId,
                                          String query, TypeAnnonce type, StatusAnnonce status,
                                          String searchedUsername, Float minPrice, Float maxPrice,
                                          TypeBien typeBien, Rooms rooms, LeaseDuration leaseDuration,
                                          Double latitude, Double longitude, Double radius
                                          , int pageNumber, int pageSize);
}
