package utm.tn.dari.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import utm.tn.dari.entities.enums.StatusAnnonce;
import utm.tn.dari.entities.enums.TypeAnnonce;

import java.time.LocalDateTime;

@Entity
@Data
public class USearchQuery {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private String description;
    private String titre;
    private Float minPrix;
    private Float maxPrix;
    private TypeAnnonce type;
    private StatusAnnonce statusAnnonce;
    private Double latitude;
    private Double longitude;
    private Double radius;
    @CreationTimestamp()
    private LocalDateTime createdAt;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;




}
