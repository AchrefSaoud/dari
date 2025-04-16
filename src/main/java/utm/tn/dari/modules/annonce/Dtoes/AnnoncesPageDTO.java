package utm.tn.dari.modules.annonce.Dtoes;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AnnoncesPageDTO {
    private List<AnnonceDTO> annonces;
    private int totalPages;
    private long totalElements;
    private int pageSize;
    private int pageNumber;
    private boolean hasNext;
    private boolean hasPrevious;
    private boolean isFirst;
    private boolean isLast;

}
