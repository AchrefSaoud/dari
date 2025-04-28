package utm.tn.dari.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import utm.tn.dari.entities.enums.StatusAnnonce;
import utm.tn.dari.entities.enums.TypeAnnonce;
import utm.tn.dari.entities.enums.TypeBien;

import java.time.LocalDateTime;

@Entity
@Data
public class USearchQuery {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private String query;
    private Float minPrix;
    private Float maxPrix;
    private TypeAnnonce type;
    private StatusAnnonce statusAnnonce;
    private Double latitude;
    private Double longitude;
    private Double radius;
    private TypeBien typeBien;
    @CreationTimestamp()
    private LocalDateTime createdAt;

    @ManyToOne(targetEntity = User.class,fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;




}
