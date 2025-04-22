package utm.tn.dari.modules.annonce.elastic.documents;


import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;


@Document(indexName = "usearchqs")
@Data
@AllArgsConstructor
public class USearchQDoc {

    @Id
    private Long id;
    private String query;
}
