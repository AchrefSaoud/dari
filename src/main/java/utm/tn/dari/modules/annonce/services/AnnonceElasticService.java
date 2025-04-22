package utm.tn.dari.modules.annonce.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utm.tn.dari.modules.annonce.elastic.documents.AnnonceDoc;
import utm.tn.dari.modules.annonce.elastic.repositories.AnnonceElasticRepo;

@Service
public class AnnonceElasticService {

    private final AnnonceElasticRepo repo;

    @Autowired
    public AnnonceElasticService(AnnonceElasticRepo repo) {
        this.repo = repo;
    }

    public AnnonceDoc createAnnonce(Long id, String title, String description) {
        AnnonceDoc annonce = new AnnonceDoc(id, title, description);
        return repo.save(annonce);
    }

}
