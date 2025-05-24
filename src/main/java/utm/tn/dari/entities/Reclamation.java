package utm.tn.dari.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import utm.tn.dari.entities.enums.ReclamationStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "reclamations")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Reclamation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String titre;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String contenu;

    @Enumerated(EnumType.STRING)
    private ReclamationStatus status = ReclamationStatus.OUVERT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User createdBy;

    @CreationTimestamp
    private LocalDateTime createdAt;

}