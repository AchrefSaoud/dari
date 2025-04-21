package utm.tn.dari.modules.abonnement.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import utm.tn.dari.entities.Abonnement;
import utm.tn.dari.entities.Rating;
import utm.tn.dari.entities.User;
import utm.tn.dari.modules.abonnement.dtos.RatingCreateDto;
import utm.tn.dari.modules.abonnement.dtos.RatingDto;
import utm.tn.dari.modules.abonnement.repositories.RatingRepository;
import utm.tn.dari.modules.user.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RatingService {
    private final RatingRepository ratingRepository;
    private final AbonnementService abonnementService;
    private final UserService userService;

    public RatingDto createRating(RatingCreateDto dto) {

        User user = userService.getUserById(dto.getUserId());
        
        Abonnement abonnement = abonnementService.getAbonnementById(dto.getAbonnementId());

        if (ratingRepository.existsByAbonnementIdAndUserId(dto.getAbonnementId(), dto.getUserId())) {
            throw new IllegalStateException("User has already rated this subscription");
        }

        Rating rating = new Rating();
        rating.setAbonnement(abonnement);
        rating.setUser(user);
        rating.setScore(dto.getScore());
        rating.setComment(dto.getComment());

        Rating savedRating = ratingRepository.save(rating);
        return convertToDto(savedRating);
    }

    public List<RatingDto> getRatingsForAbonnement(Long abonnementId) {
        if (!abonnementService.existsById(abonnementId)) {
            throw new ResourceNotFoundException("Abonnement not found with id: " + abonnementId);
        }

        return ratingRepository.findByAbonnementId(abonnementId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Double getAverageRating(Long abonnementId) {
        if (!abonnementService.existsById(abonnementId)) {
            throw new ResourceNotFoundException("Abonnement not found with id: " + abonnementId);
        }

        return ratingRepository.findAverageRatingByAbonnementId(abonnementId);
    }

    private RatingDto convertToDto(Rating rating) {
        return RatingDto.builder()
                .id(rating.getId())
                .abonnementId(rating.getAbonnement().getId())
                .userId(rating.getUser().getId())
                .username(rating.getUser().getUsername())
                .score(rating.getScore())
                .comment(rating.getComment())
                .createdAt(rating.getCreatedAt())
                .build();
    }
}