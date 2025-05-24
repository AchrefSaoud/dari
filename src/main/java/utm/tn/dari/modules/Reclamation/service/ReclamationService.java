package utm.tn.dari.modules.Reclamation.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;
import utm.tn.dari.entities.Reclamation;
import utm.tn.dari.entities.User;
import utm.tn.dari.modules.Reclamation.DTOs.CreateReclamationDTO;
import utm.tn.dari.modules.Reclamation.DTOs.ReclamationDTO;
import utm.tn.dari.modules.Reclamation.DTOs.ReclamationDetailsDTO;
import utm.tn.dari.modules.Reclamation.repo.ReclamationRepository;
import utm.tn.dari.modules.authentication.repositories.UserRepository;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReclamationService {
    private final ReclamationRepository reclamationRepository;
    private final UserRepository userRepository;

    public ReclamationService(ReclamationRepository reclamationRepository, UserRepository userRepository) {
        this.reclamationRepository = reclamationRepository;
        this.userRepository = userRepository;
    }

    public Reclamation createReclamation(CreateReclamationDTO dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        Reclamation reclamation = new Reclamation();
        reclamation.setTitre(dto.getTitre());
        reclamation.setContenu(dto.getContenu());
        reclamation.setCreatedBy(user);

        return reclamationRepository.save(reclamation);
    }

    public List<ReclamationDTO> getUserReclamations(Long userId, boolean isAdmin) {
        return reclamationRepository.findAllForUser(userId, isAdmin)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ReclamationDetailsDTO getReclamationDetails(Long id, Long userId, boolean isAdmin) {
        Reclamation reclamation = reclamationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Réclamation non trouvée"));

        if (!reclamation.getCreatedBy().getId().equals(userId) && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Accès non autorisé");
        }

        return convertToDetailsDTO(reclamation);
    }

    private ReclamationDTO convertToDTO(Reclamation reclamation) {
        ReclamationDTO dto = new ReclamationDTO();
        dto.setId(reclamation.getId());
        dto.setTitre(reclamation.getTitre());
        dto.setStatus(reclamation.getStatus());
        dto.setCreatedAt(reclamation.getCreatedAt());
        return dto;
    }

    private ReclamationDetailsDTO convertToDetailsDTO(Reclamation reclamation) {
        ReclamationDetailsDTO dto = new ReclamationDetailsDTO();
        dto.setId(reclamation.getId());
        dto.setTitre(reclamation.getTitre());
        dto.setContenu(reclamation.getContenu());
        dto.setStatus(reclamation.getStatus());
        dto.setCreatedAt(reclamation.getCreatedAt());
        return dto;
    }
}