package utm.tn.dari.modules.meuble.service;

import utm.tn.dari.modules.meuble.dto.PanierDTO;

import java.util.List;

public interface IPanierMeubleService {
    PanierDTO ajouterPanier(PanierDTO dto);
    PanierDTO getPanier(Long id);
    List<PanierDTO> getAll();
    PanierDTO modifierPanier(Long id, PanierDTO dto);
    void supprimerPanier(Long id);
    PanierDTO ajouterMeubleAuPanier(Long panierId, Long meubleId);//par défaut 1
    PanierDTO ajouterMeubleAuPanier(Long panierId, Long meubleId, int quantite);
    PanierDTO retirerMeubleDuPanier(Long panierId, Long meubleId);
    boolean contientMeuble(Long panierId, Long meubleId);
}
