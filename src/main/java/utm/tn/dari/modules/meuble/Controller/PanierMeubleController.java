package utm.tn.dari.modules.meuble.Controller;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utm.tn.dari.modules.meuble.dto.PanierDTO;
import utm.tn.dari.modules.meuble.service.IPanierMeubleService;

import java.util.List;

@RestController
@RequestMapping("/api/panier-meubles")
public class PanierMeubleController {

    @Autowired
    private IPanierMeubleService panierService;

    @PostMapping
    public ResponseEntity<PanierDTO> ajouter(@RequestBody PanierDTO dto) {
        return ResponseEntity.ok(panierService.ajouterPanier(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PanierDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(panierService.getPanier(id));
    }

    @GetMapping
    public ResponseEntity<List<PanierDTO>> getAll() {
        return ResponseEntity.ok(panierService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PanierDTO> modifier(@PathVariable Long id, @RequestBody PanierDTO dto) {
        return ResponseEntity.ok(panierService.modifierPanier(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        panierService.supprimerPanier(id);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{panierId}/retirer-meuble/{meubleId}")
    public ResponseEntity<PanierDTO> retirer(
            @PathVariable Long panierId,
            @PathVariable Long meubleId) {
        return ResponseEntity.ok(panierService.retirerMeubleDuPanier(panierId, meubleId));
    }
    @PostMapping("/{panierId}/ajouter-meuble/{meubleId}")
    public ResponseEntity<PanierDTO> ajouterMeubleAuPanier(
            @PathVariable Long panierId,
            @PathVariable Long meubleId,
            @RequestParam(required = false, defaultValue = "1") int quantite) {
        return ResponseEntity.ok(panierService.ajouterMeubleAuPanier(panierId, meubleId, quantite));
    }

}
