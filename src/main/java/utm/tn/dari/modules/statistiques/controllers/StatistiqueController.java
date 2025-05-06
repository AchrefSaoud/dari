// src/main/java/utm/tn/dari/modules/statistiques/controllers/StatistiqueController.java
package utm.tn.dari.modules.statistiques.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import utm.tn.dari.modules.statistiques.dto.AbonnementTypeStatDTO;
import utm.tn.dari.modules.statistiques.service.StatistiqueService;

import java.util.List;

@RestController
@RequestMapping("/api/statistiques")
@RequiredArgsConstructor
public class StatistiqueController {

    private final StatistiqueService statistiqueService;

    @GetMapping("/abonnements-par-type")
    public List<AbonnementTypeStatDTO> getAbonnementsParType() {
        return statistiqueService.getNombreAbonnementsParType();
    }
}
