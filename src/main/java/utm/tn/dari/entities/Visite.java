package utm.tn.dari.entities;

import java.util.Date;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Visite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visite_id")
    private Long id;

    @Column(name = "date_visite")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateVisite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    @ToString.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bien_immobilier_id")
    @JsonBackReference
    @ToString.Exclude
    private BienImmobilier bienImmobilier;
}