package utm.tn.dari.modules.authentication.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import utm.tn.dari.entities.User;
import utm.tn.dari.entities.enums.Role;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findByAbonnementId(Long abonnementId);
    List<User> findAllByIdIn(List<Long> ids);

    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(concat('%', :searchTerm, '%')) OR LOWER(u.nom) LIKE LOWER(concat('%', :searchTerm, '%'))")
    Page<User> findByUsernameContainingIgnoreCaseOrNomContainingIgnoreCase(
            @Param("searchTerm") String searchTerm, 
            Pageable pageable);
    
    // For element collections we need to use a different join syntax
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles role WHERE role = :role")
    Page<User> findByRoles_Name(@Param("role") Role role, Pageable pageable);
    
    // Updated query for element collection with role as enum
    @Query("SELECT DISTINCT u FROM User u JOIN u.roles role WHERE (LOWER(u.username) LIKE LOWER(concat('%', :searchTerm, '%')) OR LOWER(u.nom) LIKE LOWER(concat('%', :searchTerm, '%'))) AND role = :role")
    Page<User> findByUsernameContainingIgnoreCaseOrNomContainingIgnoreCaseAndRoles_Name(
            @Param("searchTerm") String searchTerm,
            @Param("role") Role role,
            Pageable pageable);
}