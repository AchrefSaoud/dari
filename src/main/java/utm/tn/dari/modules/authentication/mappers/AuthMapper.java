package utm.tn.dari.modules.authentication.mappers;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import utm.tn.dari.entities.User;
import utm.tn.dari.entities.enums.Role;
import utm.tn.dari.modules.authentication.dtos.UserLoginResponseDTO;
import utm.tn.dari.modules.authentication.dtos.UserRegistrationDTO;
import utm.tn.dari.modules.authentication.dtos.UserRegistrationResponseDTO;

@Component
public class AuthMapper {

    public User fromRegistrationDTO(UserRegistrationDTO registrationDTO) {
        User user = new User();
        user.setUsername(registrationDTO.getUsername());
        user.setPassword(registrationDTO.getPassword());
        user.setNom(registrationDTO.getNom());
        user.setTelephone(registrationDTO.getTelephone());
        user.setContrats(null);
        user.setAnnonces(null);
        //user.setVisites(null);

        if (registrationDTO.getRoles() != null && !registrationDTO.getRoles().isEmpty()) {
            user.setRoles(registrationDTO.getRoles());
        } else {
            user.setRoles(Set.of(Role.ROLE_USER));
        }
        
        return user;
    }

    public UserRegistrationResponseDTO toRegistrationResponse(User user) {
        UserRegistrationResponseDTO responseDTO = new UserRegistrationResponseDTO();
        
        responseDTO.setId(user.getId());
        responseDTO.setUsername(user.getUsername());
        responseDTO.setTelephone(user.getTelephone());
        responseDTO.setNom(user.getNom());
        if (user.getRoles() != null) {
            responseDTO.setRoles(user.getRoles().stream()
                    .map(Enum::name)
                    .collect(Collectors.toSet()));
        }
        
        return responseDTO;
    }

    public UserLoginResponseDTO toLoginResponse(UserDetails userDetails, String jwt) {
        UserLoginResponseDTO responseDTO = new UserLoginResponseDTO();
        responseDTO.setUsername(userDetails.getUsername());
        responseDTO.setJwt(jwt);
        return responseDTO;
    }
}