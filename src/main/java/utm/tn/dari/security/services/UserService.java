package utm.tn.dari.security.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import utm.tn.dari.entities.User;
import utm.tn.dari.modules.authentication.repositories.UserRepository;

//user service mahich service reel mtea user mais just pour implementer l'interface UserDetailsService li nst77agouha fi spring security

@Service("securityUserService")
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private UserRepository userRepository;
    
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            user.getAuthorities()
        );
    }

    public User save(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        if (user.getPassword() == null || user.getPassword().length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters long");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public User findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}