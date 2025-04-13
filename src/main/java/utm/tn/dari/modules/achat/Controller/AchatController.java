package utm.tn.dari.modules.achat.Controller;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utm.tn.dari.entities.Annonce;
import utm.tn.dari.entities.RechercheAchat;
import utm.tn.dari.modules.achat.service.AchatService;

import java.util.List;

@RestController
@RequestMapping("/api/achat")
@RequiredArgsConstructor

public class AchatController {

    private final AchatService achatService;

    @PostMapping("/recherche")
    public ResponseEntity<RechercheAchat> enregistrerRecherche(@RequestBody RechercheAchat recherche) {
        return ResponseEntity.ok(achatService.enregistrerRecherche(recherche));
    }

    @PostMapping("/rechercher")
    public ResponseEntity<List<Annonce>> rechercher(@RequestBody RechercheAchat recherche) {
        return ResponseEntity.ok(achatService.rechercherAnnonces(recherche));
    }
}
