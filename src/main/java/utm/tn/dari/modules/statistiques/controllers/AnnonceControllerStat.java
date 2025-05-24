package utm.tn.dari.modules.statistiques.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import utm.tn.dari.modules.statistiques.dto.AnnonceStatistiquesDTO;
import utm.tn.dari.modules.statistiques.service.AnnonceServiceStat;
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor

@RestController
@RequestMapping("/annonces")
public class AnnonceControllerStat {

    private final AnnonceServiceStat annonceService;

    @GetMapping("/statistiques")
    public AnnonceStatistiquesDTO getStatistiques() {
        return annonceService.getStatistiques();
    }
}
