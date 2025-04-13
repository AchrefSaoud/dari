package utm.tn.dari.modules.meuble.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import utm.tn.dari.entities.Meuble;
import utm.tn.dari.entities.User;
import utm.tn.dari.modules.authentication.repositories.UserRepository;
import utm.tn.dari.modules.meuble.dto.MeubleDTO;
import utm.tn.dari.modules.meuble.mapper.MeubleMapper;
import utm.tn.dari.modules.meuble.repository.MeubleRepository;


import java.util.List;

@Service
@RequiredArgsConstructor
public class MeubleServiceImpl implements IMeubleService {

    private final MeubleRepository meubleRepo;
    private final UserRepository userRepo;

    @Override
    public MeubleDTO ajouterMeuble(MeubleDTO dto) {
        User vendeur = userRepo.findById(dto.getVendeurId()).orElseThrow();
        Meuble meuble = MeubleMapper.toEntity(dto, vendeur);
        return MeubleMapper.toDto(meubleRepo.save(meuble));
    }

    @Override
    public List<MeubleDTO> listerTousLesMeubles() {
        return meubleRepo.findAll().stream()
                .map(MeubleMapper::toDto)
                .toList();
    }

    @Override
    public void supprimerMeuble(Long id) {
        meubleRepo.deleteById(id);
    }

    @Override
    public MeubleDTO getById(Long id) {
        return meubleRepo.findById(id)
                .map(MeubleMapper::toDto)
                .orElse(null);
    }
}
