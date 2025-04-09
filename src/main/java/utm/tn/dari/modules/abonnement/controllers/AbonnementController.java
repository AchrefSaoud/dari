package utm.tn.dari.modules.abonnement.controllers;



import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import utm.tn.dari.entities.Abonnement;
import utm.tn.dari.modules.abonnement.services.AbonnementService;

import java.util.List;

@RestController
@RequestMapping("/api/abonnements")
@RequiredArgsConstructor
public class AbonnementController {
    private final AbonnementService abonnementService;

    @PostMapping("/create")
    public Abonnement create(@RequestBody Abonnement abonnement) {
        return abonnementService.createAbonnement(abonnement);
    }

    @PutMapping("/update/{id}")
    public Abonnement update(@PathVariable Long id, @RequestBody Abonnement abonnement) {
        return abonnementService.updateAbonnement(id, abonnement);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        abonnementService.deleteAbonnement(id);
    }

    @GetMapping("/list")
    public List<Abonnement> list() {
        return abonnementService.getAllAbonnements();
    }
}
