package utm.tn.dari.modules.meuble.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utm.tn.dari.entities.Meuble;
import utm.tn.dari.entities.PanierMeuble;
import utm.tn.dari.entities.PanierMeubleItem;
import utm.tn.dari.entities.User;
import utm.tn.dari.modules.authentication.repositories.UserRepository;
import utm.tn.dari.modules.meuble.dto.PanierDTO;
import utm.tn.dari.modules.meuble.mapper.PanierMeubleMapper;
import utm.tn.dari.modules.meuble.repository.MeubleRepository;
import utm.tn.dari.modules.meuble.repository.PanierMeubleRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PanierMeubleServiceImpl implements IPanierMeubleService {

    @Autowired
    private PanierMeubleRepository panierRepo;

    @Autowired
    private MeubleRepository meubleRepo;

    @Autowired
    private UserRepository userRepo;

    @Override
    public PanierDTO ajouterPanier(PanierDTO dto) {
        User acheteur = userRepo.findById(dto.getAcheteurId()).orElseThrow(() -> new RuntimeException("Acheteur non trouvable"));
        PanierMeuble panier = PanierMeubleMapper.toEntity(dto, acheteur);
        panier.setTotal(0); // Initialiser à 0
        panier = panierRepo.save(panier);
        return PanierMeubleMapper.toDto(panier);
    }

    @Override
    public PanierDTO getPanier(Long id) {
        PanierMeuble panier = panierRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Panier non trouvé"));
        return PanierMeubleMapper.toDto(panier);
    }

    @Override
    public List<PanierDTO> getAll() {
        return panierRepo.findAll().stream()
                .map(PanierMeubleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PanierDTO modifierPanier(Long id, PanierDTO dto) {
        PanierMeuble panier = panierRepo.findById(id).orElseThrow(()-> new RuntimeException("Panier non trouvé"));;
        panier.setItems(PanierMeubleMapper.mapItems(dto.getItems(), panier));
        panier.setTotal(
                panier.getItems().stream().map(PanierMeubleItem::getSousTotal).reduce(0f, Float::sum)
        );
        return PanierMeubleMapper.toDto(panierRepo.save(panier));
    }

    @Override
    public void supprimerPanier(Long id) {
        panierRepo.deleteById(id);
    }
    @Override
    public PanierDTO ajouterMeubleAuPanier(Long panierId, Long meubleId) {
        return ajouterMeubleAuPanier(panierId, meubleId, 1);
    }
    @Override
    public PanierDTO ajouterMeubleAuPanier(Long panierId, Long meubleId, int quantite) {
        PanierMeuble panier = panierRepo.findById(panierId).orElseThrow();
        Meuble meuble = meubleRepo.findById(meubleId).orElseThrow();

        Optional<PanierMeubleItem> existingItem = panier.getItems().stream()
                .filter(item -> item.getMeuble().getId().equals(meubleId))
                .findFirst();

        if (existingItem.isPresent()) {
            PanierMeubleItem item = existingItem.get();
            item.setQuantite(item.getQuantite() + quantite);
            item.setSousTotal(item.getQuantite() * meuble.getPrix());
        } else {
            PanierMeubleItem item = new PanierMeubleItem();
            item.setMeuble(meuble);
            item.setQuantite(quantite);
            item.setSousTotal(quantite * meuble.getPrix());
            item.setPanier(panier);
            panier.getItems().add(item);
        }

        float total = panier.getItems().stream()
                .map(PanierMeubleItem::getSousTotal)
                .reduce(0f, Float::sum);

        panier.setTotal(total);
        panier = panierRepo.save(panier);
        return PanierMeubleMapper.toDto(panier);
    }
    @Override
    public PanierDTO retirerMeubleDuPanier(Long panierId, Long meubleId) {
        PanierMeuble panier = panierRepo.findById(panierId)
                .orElseThrow(() -> new RuntimeException("Panier non trouvé"));

        panier.getItems().removeIf(item -> item.getMeuble().getId().equals(meubleId));

        panier.setTotal((float) panier.getItems().stream()
                .mapToDouble(PanierMeubleItem::getSousTotal)
                .sum());

        panierRepo.save(panier);
        return PanierMeubleMapper.toDto(panier);
    }
    @Override
    public boolean contientMeuble(Long panierId, Long meubleId) {
        PanierMeuble panier = panierRepo.findById(panierId)
                .orElseThrow(() -> new RuntimeException("Panier non trouvé"));
        return panier.getItems().stream()
                .anyMatch(item -> item.getMeuble().getId().equals(meubleId));
    }
}
