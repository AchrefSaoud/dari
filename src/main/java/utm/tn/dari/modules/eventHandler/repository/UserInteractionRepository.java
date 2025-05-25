package utm.tn.dari.modules.eventHandler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import utm.tn.dari.UserInteraction;

@Repository
public interface UserInteractionRepository  extends JpaRepository<UserInteraction, Long> {

    // This repository will handle UserInteraction entities.
    // You can add custom query methods here if needed.
}
