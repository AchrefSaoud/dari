package utm.tn.dari.modules.annonce.elastic.repositories;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import utm.tn.dari.modules.annonce.elastic.documents.USearchQDoc;

import java.util.List;

public interface USearchQElasticRepo extends ElasticsearchRepository<USearchQDoc,Long> {

    @Query("{\"bool\": { \"should\": [ " +
            "{\"wildcard\": { \"queryMatches\": \"*?0*\" }}," +
            "{\"match\": { \"queryMatches\": { \"query\": \"?0\", \"analyzer\": \"ngram_analyzer\" }}}" +
            "]}}")
    List<USearchQDoc> searchByQueryWithWildcardAndNgram(String query);

    List<USearchQDoc> findAllByQueryMatches(String query);


}
