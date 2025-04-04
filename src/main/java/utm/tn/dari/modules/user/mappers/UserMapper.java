package utm.tn.dari.modules.user.mappers;

import org.springframework.stereotype.Component;

import utm.tn.dari.entities.User;
import utm.tn.dari.modules.user.dtos.UserDto;
import utm.tn.dari.modules.user.dtos.UserResponseDto;
import utm.tn.dari.modules.user.dtos.UserUpdateDto;


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
    }
}