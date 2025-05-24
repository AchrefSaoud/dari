package utm.tn.dari.modules.annonce.repositories;


import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import utm.tn.dari.entities.Annonce;
import utm.tn.dari.entities.User;

@Repository
public interface AnnonceRepository extends JpaRepository<Annonce,Long>, JpaSpecificationExecutor<Annonce> {

    @EntityGraph(attributePaths = {"attachmentPaths", "user"})
    @NonNull
    Page<Annonce> findAll(Specification<Annonce> annonceSpecification,@NonNull Pageable pageable);
    Page<Annonce> findAllByUser(User user, Pageable pageable);

    @EntityGraph(attributePaths = {"demandes","user"})
    @Query("SELECT a FROM Annonce a JOIN a.demandes d WHERE d.user = ?1")
    Page<Annonce> findRequestedByUser(User user, Pageable pageable);


}
