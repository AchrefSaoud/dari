package utm.tn.dari.modules.authentication.services;

import utm.tn.dari.modules.authentication.dtos.UserLoginDTO;
import utm.tn.dari.modules.authentication.dtos.UserLoginResponseDTO;
import utm.tn.dari.modules.authentication.dtos.UserRegistrationDTO;
import utm.tn.dari.modules.authentication.dtos.UserRegistrationResponseDTO;

public interface AuthService {
    UserRegistrationResponseDTO register(UserRegistrationDTO registrationDTO);
    UserLoginResponseDTO login(UserLoginDTO loginDTO);
}