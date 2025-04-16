package utm.tn.dari.modules.meuble.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utm.tn.dari.entities.Meuble;
import utm.tn.dari.entities.User;
import utm.tn.dari.modules.meuble.service.AuthenticationUtils;
import utm.tn.dari.modules.meuble.dto.MeubleCreateDTO;
import utm.tn.dari.modules.meuble.dto.MeubleDTO;
import utm.tn.dari.modules.meuble.mapper.MeubleMapper;
import utm.tn.dari.modules.meuble.repository.MeubleRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeubleServiceImpl implements IMeubleService {

    private final MeubleRepository meubleRepository;
    private final AuthenticationUtils authenticationUtils;

    @Override
    @Transactional
    public MeubleDTO ajouterMeuble(MeubleCreateDTO dto) {
        // Récupère automatiquement l'utilisateur authentifié
        User vendeur = authenticationUtils.getCurrentUser();

        Meuble meuble = MeubleMapper.toEntity(dto, vendeur);
        Meuble savedMeuble = meubleRepository.save(meuble);
        return MeubleMapper.toDto(savedMeuble);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MeubleDTO> listerTousLesMeubles() {
        return meubleRepository.findAll().stream()
                .map(MeubleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void supprimerMeuble(Long id) {
        Meuble meuble = meubleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meuble non trouvé avec l'ID: " + id));

        // Vérification que l'utilisateur actuel est bien le propriétaire
        User currentUser = authenticationUtils.getCurrentUser();
        if (!meuble.getVendeur().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Vous n'êtes pas autorisé à supprimer ce meuble");
        }

        meubleRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public MeubleDTO getById(Long id) {
        return meubleRepository.findById(id)
                .map(MeubleMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Meuble non trouvé avec l'ID: " + id));
    }

    @Override
    @Transactional
    public MeubleDTO modifierMeuble(Long id, MeubleCreateDTO dto) {
        Meuble existingMeuble = meubleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meuble non trouvé avec l'ID: " + id));

        // Vérification que l'utilisateur actuel est bien le propriétaire
        User currentUser = authenticationUtils.getCurrentUser();
        if (!existingMeuble.getVendeur().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Vous n'êtes pas autorisé à modifier ce meuble");
        }

        MeubleMapper.updateEntity(existingMeuble, dto);
        // Le vendeur reste le même, pas besoin de le changer

        Meuble updatedMeuble = meubleRepository.save(existingMeuble);
        return MeubleMapper.toDto(updatedMeuble);
    }
}