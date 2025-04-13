package utm.tn.dari.modules.meuble.Controller;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utm.tn.dari.modules.meuble.dto.MeubleDTO;
import utm.tn.dari.modules.meuble.service.IMeubleService;

import java.util.List;

@RestController
@RequestMapping("/api/meubles")
@RequiredArgsConstructor
public class MeubleController {

    private final IMeubleService meubleService;

    @PostMapping
    public ResponseEntity<MeubleDTO> ajouter(@RequestBody MeubleDTO dto) {
        return ResponseEntity.ok(meubleService.ajouterMeuble(dto));
    }

    @GetMapping
    public List<MeubleDTO> all() {
        return meubleService.listerTousLesMeubles();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        meubleService.supprimerMeuble(id);
    }
    @PutMapping("/{id}")
    public ResponseEntity<MeubleDTO> modifier(@PathVariable Long id, @RequestBody MeubleDTO dto) {
        return ResponseEntity.ok(meubleService.modifierMeuble(id, dto));
    }
    @GetMapping("/{id}")
    public ResponseEntity<MeubleDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(meubleService.getById(id));
    }
}
