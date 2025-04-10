package utm.tn.dari.modules.user.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import utm.tn.dari.modules.user.dtos.UserResponseDto;
import utm.tn.dari.modules.user.dtos.UserStatusDto;
import utm.tn.dari.modules.user.dtos.UserUpdateDto;
import utm.tn.dari.modules.user.services.UserService;

import java.util.Map;

@RestController("userManagementController")
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Endpoints for managing user accounts and profiles")
@SecurityRequirement(name = "bearerAuth") 
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get user by ID", 
        description = "Retrieves user details by their unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "User details retrieved successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE, 
                schema = @Schema(implementation = UserResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "User not found",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE, 
                schema = @Schema(implementation = Map.class)
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE, 
                schema = @Schema(implementation = Map.class)
            )
        )
    })
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update user profile", 
        description = "Updates user information including phone number, name, and password"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "User updated successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE, 
                schema = @Schema(implementation = UserResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Validation error - Invalid input data",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE, 
                schema = @Schema(implementation = Map.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "User not found",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE, 
                schema = @Schema(implementation = Map.class)
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE, 
                schema = @Schema(implementation = Map.class)
            )
        )
    })
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id,
            @RequestBody UserUpdateDto userUpdateDto) {
        return ResponseEntity.ok(userService.updateUser(id, userUpdateDto));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(
        summary = "Update user status", 
        description = "Admin endpoint to ban/unban a user account"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "User status updated successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE, 
                schema = @Schema(implementation = UserResponseDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Forbidden - Only admins can perform this action",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE, 
                schema = @Schema(implementation = Map.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "User not found",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE, 
                schema = @Schema(implementation = Map.class)
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE, 
                schema = @Schema(implementation = Map.class)
            )
        )
    })
    public ResponseEntity<UserResponseDto> updateUserStatus(
            @PathVariable Long id,
            @RequestBody UserStatusDto statusDto) {
        return ResponseEntity.ok(userService.banUser(id, statusDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(
        summary = "Delete user account", 
        description = "Admin endpoint to permanently delete a user account"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204", 
            description = "User deleted successfully",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Forbidden - Only admins can perform this action",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE, 
                schema = @Schema(implementation = Map.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "User not found",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE, 
                schema = @Schema(implementation = Map.class)
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE, 
                schema = @Schema(implementation = Map.class)
            )
        )
    })
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(
        summary = "Get all users", 
        description = "Admin endpoint to retrieve paginated list of all users"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Users retrieved successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE, 
                schema = @Schema(implementation = Page.class)
            )
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Forbidden - Only admins can perform this action",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE, 
                schema = @Schema(implementation = Map.class)
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE, 
                schema = @Schema(implementation = Map.class)
            )
        )
    })
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }
}