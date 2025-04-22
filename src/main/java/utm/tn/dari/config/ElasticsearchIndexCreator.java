package utm.tn.dari.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Component;
import utm.tn.dari.modules.annonce.elastic.documents.AnnonceDoc;

import java.util.List;
import java.util.Map;

@Component
public class ElasticsearchIndexCreator {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @PostConstruct
    public void createIndexWithCustomSettings() {
        IndexOperations indexOps = elasticsearchOperations.indexOps(AnnonceDoc.class);

        if (!indexOps.exists()) {
            Map<String, Object> ngramTokenizer = Map.of(
                    "type", "ngram",
                    "min_gram", 2,
                    "max_gram", 10,
                    "token_chars", List.of("letter", "digit")
            );

            Map<String, Object> analyzer = Map.of(
                    "type", "custom",
                    "tokenizer", "ngram_tokenizer",
                    "filter", List.of("lowercase")
            );

            Map<String, Object> analysis = Map.of(
                    "tokenizer", Map.of("ngram_tokenizer", ngramTokenizer),
                    "analyzer", Map.of("ngram_analyzer", analyzer)
            );

            Map<String, Object> settings = Map.of(
                    "analysis", analysis,
                    "max_ngram_diff", 8
            );

        }
    }
}
