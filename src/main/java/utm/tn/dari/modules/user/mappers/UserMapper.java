package utm.tn.dari.modules.user.mappers;

import org.springframework.stereotype.Component;

import utm.tn.dari.entities.User;
import utm.tn.dari.entities.Abonnement;
import utm.tn.dari.modules.user.dtos.UserDto;
import utm.tn.dari.modules.user.dtos.UserResponseDto;
import utm.tn.dari.modules.user.dtos.UserUpdateDto;
import utm.tn.dari.modules.abonnement.dtos.AbonnementDto;

@Component
public class UserMapper {

    public UserResponseDto toResponseDto(User user) {
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(user.getId());
        responseDto.setUsername(user.getUsername());
        responseDto.setTelephone(user.getTelephone());
        responseDto.setNom(user.getNom());
        responseDto.setRoles(user.getRoles());
        responseDto.setActive(user.isActive());
        responseDto.setProfilePicture(user.getProfilePicture());
        
        // Map abonnement if it exists
        if (user.getAbonnement() != null) {
            responseDto.setAbonnement(toAbonnementDto(user.getAbonnement()));
        }
        
        return responseDto;
    }

    public User toEntity(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setTelephone(userDto.getTelephone());
        user.setNom(userDto.getNom());
        user.setRoles(userDto.getRoles());
        return user;
    }

    public void updateUserFromDto(UserUpdateDto dto, User user) {
        if (dto.getTelephone() != null) {
            user.setTelephone(dto.getTelephone());
        }
        if (dto.getNom() != null) {
            user.setNom(dto.getNom());
        }
        if (dto.getPassword() != null) {
            user.setPassword(dto.getPassword());
        }
        if (dto.getProfilePicture() != null) {
            user.setProfilePicture(dto.getProfilePicture());
        }
    }
    
    // Helper method to map Abonnement entity to AbonnementDto
    private AbonnementDto toAbonnementDto(Abonnement abonnement) {
        if (abonnement == null) {
            return null;
        }
        
        AbonnementDto dto = new AbonnementDto();
        dto.setId(abonnement.getId());
        dto.setNom(abonnement.getNom());
        dto.setDescription(abonnement.getDescription());
        dto.setPrix(abonnement.getPrix());
        dto.setType(abonnement.getType());
        
        // Calculate average rating and count if ratings exist
        if (abonnement.getRatings() != null && !abonnement.getRatings().isEmpty()) {
            double avgRating = abonnement.getRatings().stream()
                .mapToDouble(rating -> rating.getScore()) // Assuming Rating has a getNote() method
                .average()
                .orElse(0.0);
            dto.setAverageRating(avgRating);
            dto.setRatingCount(abonnement.getRatings().size());
        } else {
            dto.setAverageRating(0.0);
            dto.setRatingCount(0);
        }
        
        return dto;
    }
}