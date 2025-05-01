package utm.tn.dari.modules.user.services.serviceImpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import utm.tn.dari.entities.User;
import utm.tn.dari.entities.enums.Role;
import utm.tn.dari.modules.authentication.repositories.UserRepository;
import utm.tn.dari.modules.user.dtos.UserResponseDto;
import utm.tn.dari.modules.user.dtos.UserStatusDto;
import utm.tn.dari.modules.user.dtos.UserUpdateDto;
import utm.tn.dari.modules.user.exceptions.ResourceNotFoundException;
import utm.tn.dari.modules.user.mappers.UserMapper;
import utm.tn.dari.modules.user.services.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toResponseDto(user);
    }

    @Override
    public UserResponseDto updateUser(Long id, UserUpdateDto userUpdateDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        userMapper.updateUserFromDto(userUpdateDto, user);
        User updatedUser = userRepository.save(user);
        return userMapper.toResponseDto(updatedUser);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponseDto banUser(Long id, UserStatusDto statusDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        user.setActive(statusDto.isActive());
        User updatedUser = userRepository.save(user);
        return userMapper.toResponseDto(updatedUser);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponseDto> getAllUsers(Pageable pageable, String searchTerm, String roleStr) {
        if (searchTerm != null && !searchTerm.isEmpty() && roleStr != null && !roleStr.isEmpty()) {
            // Convert string role to enum
            Role role = convertStringToRole(roleStr);
            
            // Filter by both search term and role
            return userRepository.findByUsernameContainingIgnoreCaseOrNomContainingIgnoreCaseAndRoles_Name(
                    searchTerm, role, pageable)
                    .map(userMapper::toResponseDto);
        } else if (searchTerm != null && !searchTerm.isEmpty()) {
            // Filter only by search term
            return userRepository.findByUsernameContainingIgnoreCaseOrNomContainingIgnoreCase(
                    searchTerm, pageable)
                    .map(userMapper::toResponseDto);
        } else if (roleStr != null && !roleStr.isEmpty()) {
            // Convert string role to enum
            Role role = convertStringToRole(roleStr);
            
            // Filter only by role
            return userRepository.findByRoles_Name(role, pageable)
                    .map(userMapper::toResponseDto);
        } else {
            // No filters - return all
            return userRepository.findAll(pageable)
                    .map(userMapper::toResponseDto);
        }
    }
    
    private Role convertStringToRole(String roleStr) {
        try {
            if (!roleStr.startsWith("ROLE_")) {
                roleStr = "ROLE_" + roleStr.toUpperCase();
            } else {
                roleStr = roleStr.toUpperCase();
            }
            return Role.valueOf(roleStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + roleStr);
        }
    }
}