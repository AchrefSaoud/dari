package utm.tn.dari.modules.meuble.service;



import utm.tn.dari.modules.meuble.dto.MeubleDTO;

import java.util.List;

public interface IMeubleService {
    MeubleDTO ajouterMeuble(MeubleDTO dto);
    List<MeubleDTO> listerTousLesMeubles();
    void supprimerMeuble(Long id);
    MeubleDTO getById(Long id);
}