package utm.tn.dari.modules.authentication.services.serviceImpl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import utm.tn.dari.entities.User;
import utm.tn.dari.modules.authentication.dtos.UserLoginDTO;
import utm.tn.dari.modules.authentication.dtos.UserLoginResponseDTO;
import utm.tn.dari.modules.authentication.dtos.UserRegistrationDTO;
import utm.tn.dari.modules.authentication.dtos.UserRegistrationResponseDTO;
import utm.tn.dari.modules.authentication.mappers.AuthMapper;
import utm.tn.dari.modules.authentication.services.AuthService;
import utm.tn.dari.security.jwt.JwtUtil;
import utm.tn.dari.security.services.UserService;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthMapper authMapper;

    @Override
    public UserRegistrationResponseDTO register(UserRegistrationDTO registrationDTO) {
        if (userService.findByUsername(registrationDTO.getUsername()) != null) {
            return null;
        }

        User user = authMapper.fromRegistrationDTO(registrationDTO);
        User savedUser = userService.save(user);

        return authMapper.toRegistrationResponse(savedUser);
    }

    @Override
    public UserLoginResponseDTO login(UserLoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
        );

        final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        final String jwt = jwtUtil.generateToken(userDetails);

        return authMapper.toLoginResponse(userDetails, jwt);
    }
}