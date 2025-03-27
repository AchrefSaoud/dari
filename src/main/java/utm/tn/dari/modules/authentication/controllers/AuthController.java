package utm.tn.dari.modules.authentication.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import utm.tn.dari.modules.authentication.dtos.UserLoginDTO;
import utm.tn.dari.modules.authentication.dtos.UserLoginResponseDTO;
import utm.tn.dari.modules.authentication.dtos.UserRegistrationDTO;
import utm.tn.dari.modules.authentication.dtos.UserRegistrationResponseDTO;
import utm.tn.dari.modules.authentication.services.AuthService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User Registration and Authentication Endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
        summary = "Register a new user", 
        description = "Allows a new user to register in the system with required details"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "User successfully registered",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE, 
                schema = @Schema(implementation = UserRegistrationResponseDTO.class)
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
            responseCode = "409", 
            description = "Conflict - Username already exists",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE, 
                schema = @Schema(implementation = Map.class)
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error during registration",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE, 
                schema = @Schema(implementation = Map.class)
            )
        )
    })
    public ResponseEntity<?> register(@RequestBody UserRegistrationDTO registrationDTO) {
        List<String> validationErrors = registrationDTO.validate();
        if (!validationErrors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", validationErrors));
        }

        try {
            UserRegistrationResponseDTO responseDTO = authService.register(registrationDTO);
            if (responseDTO == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Username already exists"));
            }
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Registration failed: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    @Operation(
        summary = "User Login", 
        description = "Authenticate user and generate JWT token"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "User successfully authenticated",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE, 
                schema = @Schema(implementation = UserLoginResponseDTO.class)
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
            responseCode = "401", 
            description = "Unauthorized - Invalid credentials",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE, 
                schema = @Schema(implementation = Map.class)
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error during authentication",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE, 
                schema = @Schema(implementation = Map.class)
            )
        )
    })
    public ResponseEntity<?> login(@RequestBody UserLoginDTO loginDTO) {
        List<String> validationErrors = loginDTO.validate();
        if (!validationErrors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", validationErrors));
        }

        try {
            return ResponseEntity.ok(authService.login(loginDTO));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid username or password"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Authentication failed: " + e.getMessage()));
        }
    }
}