package utm.tn.dari.modules.user.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import utm.tn.dari.modules.user.dtos.UserResponseDto;
import utm.tn.dari.modules.user.dtos.UserStatusDto;
import utm.tn.dari.modules.user.dtos.UserUpdateDto;

public interface UserService {
    UserResponseDto getUserById(Long id);

    UserResponseDto updateUser(Long id, UserUpdateDto userUpdateDto);

    UserResponseDto banUser(Long id, UserStatusDto statusDto);

    void deleteUser(Long id);
    
    Page<UserResponseDto> getAllUsers(Pageable pageable, String searchTerm, String role);
}