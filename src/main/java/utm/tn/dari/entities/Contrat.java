package utm.tn.dari.entities;

import java.util.Date;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Contrat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    @Column(name = "date_signature")
    @Temporal(TemporalType.DATE)
    private Date dateSignature;

    @OneToOne(mappedBy = "contrat", fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    private BienImmobilier bienImmobilier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    @ToString.Exclude
    private User user;
}