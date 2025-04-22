package utm.tn.dari.modules.annonce.elastic.repositories;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import utm.tn.dari.entities.Annonce;
import utm.tn.dari.modules.annonce.elastic.documents.AnnonceDoc;

import java.util.List;


@Repository
public interface AnnonceElasticRepo extends ElasticsearchRepository<AnnonceDoc, Long> {

    List<AnnonceDoc> findAllByTitleMatches(String title);

    List<AnnonceDoc> findAllByDescriptionMatches(String desc);
}
