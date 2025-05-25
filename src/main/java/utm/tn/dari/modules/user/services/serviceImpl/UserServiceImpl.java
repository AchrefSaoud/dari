
package utm.tn.dari.modules.user.services.serviceImpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import utm.tn.dari.entities.User;
import utm.tn.dari.entities.enums.Role;
import utm.tn.dari.modules.authentication.repositories.UserRepository;
import utm.tn.dari.modules.user.dtos.UserResponseDto;
import utm.tn.dari.modules.user.dtos.UserStatusDto;
import utm.tn.dari.modules.user.dtos.UserUpdateDto;
import utm.tn.dari.modules.user.exceptions.ResourceNotFoundException;
import utm.tn.dari.modules.user.mappers.UserMapper;
import utm.tn.dari.modules.user.services.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Value("${file.upload.directory:uploads/profile-pictures/}")
    private String uploadDirectory;

    @Value("${server.port:8080}") 
    private String serverPort;

    @Value("${file.upload.max-size:5242880}") // 5MB default
    private long maxFileSize;

    private final List<String> allowedFileTypes = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findByIdWithAbonnement(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
            return userMapper.toResponseDto(user);
    }

    @Override
    public UserResponseDto updateUser(Long id, UserUpdateDto userUpdateDto) {
        User user = userRepository.findByIdWithAbonnement(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        userMapper.updateUserFromDto(userUpdateDto, user);
        User updatedUser = userRepository.save(user);
        return userMapper.toResponseDto(updatedUser);
    }

    @Override
    public UserResponseDto updateUserWithFile(Long id, String telephone, String nom, String password, MultipartFile profilePicture) {
        User user = userRepository.findByIdWithAbonnement(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        // Update basic fields
        if (telephone != null && !telephone.trim().isEmpty()) {
            user.setTelephone(telephone.trim());
        }
        if (nom != null && !nom.trim().isEmpty()) {
            user.setNom(nom.trim());
        }
        if (password != null && !password.trim().isEmpty()) {
            if (password.length() < 8) {
                throw new IllegalArgumentException("Password must be at least 8 characters long");
            }
            user.setPassword(password); // You should encode this password before saving
        }
        
        // Handle profile picture upload
        if (profilePicture != null && !profilePicture.isEmpty()) {
            try {
                String fileName = saveProfilePicture(profilePicture);
                user.setProfilePicture(fileName);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save profile picture: " + e.getMessage(), e);
            }
        }
        
        User updatedUser = userRepository.save(user);
        return userMapper.toResponseDto(updatedUser);
    }

    private String saveProfilePicture(MultipartFile file) throws IOException {
        // Validate file size
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of " + (maxFileSize / 1024 / 1024) + "MB");
        }
        
        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !allowedFileTypes.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Invalid file type. Only JPEG, PNG, GIF, and WebP images are allowed");
        }
        
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDirectory);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        
        // Save file
         Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
       return "http://localhost:" + serverPort + "/uploads/profile-pictures/" + uniqueFilename;
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
        
        // Delete profile picture file if it exists
        if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
            try {
                Path filePath = Paths.get(uploadDirectory, user.getProfilePicture());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                // Log the error but don't fail the user deletion
                System.err.println("Failed to delete profile picture file: " + e.getMessage());
            }
        }
        
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