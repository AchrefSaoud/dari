package utm.tn.dari.modules.achat.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import utm.tn.dari.entities.*;
import utm.tn.dari.modules.achat.repositories.RechercheAchatRepository;
import utm.tn.dari.modules.annonce.repositories.AnnonceRepository;
import utm.tn.dari.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AchatService {

    private final RechercheAchatRepository rechercheRepo;
    private final AnnonceRepository annonceRepo;

    // Sauvegarder une recherche
    public RechercheAchat enregistrerRecherche(RechercheAchat recherche) {
        return rechercheRepo.save(recherche);
    }

    // Rechercher des annonces qui correspondent aux critères
    public List<Annonce> rechercherAnnonces(RechercheAchat r) {
        return annonceRepo.findByCriteres(r.getLocalisation(), r.getMinPrix(), r.getMaxPrix());
    }

    // Annonces correspondantes à toutes les recherches utilisateurs
    public void verifierAnnoncesPourNotification(Annonce nouvelleAnnonce) {
        List<RechercheAchat> recherches = rechercheRepo.findAll();
        for (RechercheAchat recherche : recherches) {
            if (correspond(nouvelleAnnonce, recherche)) {
                // Appel à NotificationService
                System.out.println("🔔 Notifier utilisateur : " + recherche.getUser().getEmail());
            }
        }
    }

    // Vérification si une annonce correspond à une recherche
    private boolean correspond(Annonce annonce, RechercheAchat r) {
        if (r.getLocalisation() != null && !annonce.getBienImmobilier().getLocalisation().contains(r.getLocalisation())) return false;
        if (r.getMinPrix() != null && annonce.getPrix() < r.getMinPrix()) return false;
        if (r.getMaxPrix() != null && annonce.getPrix() > r.getMaxPrix()) return false;
        return true;
    }
}