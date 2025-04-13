package utm.tn.dari.modules.meuble.service;



import utm.tn.dari.modules.meuble.dto.MeubleDTO;

import java.util.List;

public interface IMeubleService {
    MeubleDTO ajouterMeuble(MeubleDTO dto);       // Create
    List<MeubleDTO> listerTousLesMeubles();       // Read All
    MeubleDTO getById(Long id);                   // Read One
    MeubleDTO modifierMeuble(Long id, MeubleDTO dto); // Update
    void supprimerMeuble(Long id);                // Delete
}
