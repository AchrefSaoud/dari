package utm.tn.dari;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import utm.tn.dari.entities.enums.*;
import utm.tn.dari.modules.annonce.Dtoes.AnnonceDTO;
import utm.tn.dari.modules.annonce.services.RecommendationService;

@SpringBootTest
public class RecommendationServiceImplTest {

	@Autowired
	private RecommendationService recommendationService;

}
