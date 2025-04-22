package utm.tn.dari.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.stereotype.Component;
import utm.tn.dari.modules.annonce.elastic.documents.AnnonceDoc;
import utm.tn.dari.modules.annonce.elastic.documents.USearchQDoc;

import java.util.List;
import java.util.Map;

@Component
public class ElasticsearchIndexCreator {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @PostConstruct
    public void createIndexWithCustomSettings() {
        createIndexIfNotExists(AnnonceDoc.class);
        createIndexIfNotExists(USearchQDoc.class);
    }

    private void createIndexIfNotExists(Class<?> documentClass) {
        IndexOperations indexOps = elasticsearchOperations.indexOps(documentClass);

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

            indexOps.create(settings);

            // Mapping with ngram analyzer for specific fields
            Map<String, Object> mapping = Map.of(
                    "properties", Map.of(
                            "query", Map.of(
                                    "type", "text",
                                    "analyzer", "ngram_analyzer",
                                    "search_analyzer", "standard"
                            ),
                            "title", Map.of(
                                    "type", "text",
                                    "analyzer", "ngram_analyzer",
                                    "search_analyzer", "standard"
                            ),
                            "description", Map.of(
                                    "type", "text",
                                    "analyzer", "ngram_analyzer",
                                    "search_analyzer", "standard"
                            )
                    )
            );

            indexOps.putMapping(Document.from(mapping));
        }
    }
}
