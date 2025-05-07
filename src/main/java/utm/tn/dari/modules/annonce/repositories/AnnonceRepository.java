package utm.tn.dari.modules.annonce.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import utm.tn.dari.entities.Annonce;
import utm.tn.dari.entities.User;
import utm.tn.dari.entities.enums.TypeAnnonce;

import java.util.List;

@Repository
public interface AnnonceRepository extends JpaRepository<Annonce,Long>, JpaSpecificationExecutor<Annonce> {

    Page<Annonce> findAllByUser(User user, Pageable pageable);

    List<Annonce> findByTypeAnnonce(TypeAnnonce type);
}
