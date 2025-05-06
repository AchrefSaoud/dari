    package utm.tn.dari.modules.visite.repo;

    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;
    import utm.tn.dari.entities.Visite;

    import java.time.LocalDateTime;
    import java.util.List;

    public interface VisiteRepo extends JpaRepository<Visite, Long> {
        @Query("SELECT v FROM Visite v " +
                "WHERE v.annonce.id = :annonceId " +
                "AND v.startTime > :now " +
                "AND v.client IS NULL")
        List<Visite> findAvailableSlots(
                @Param("annonceId") Long annonceId,
                @Param("now") LocalDateTime now);

        @Query("SELECT v FROM Visite v " +
                "WHERE v.owner.id = :userId " +
                "AND v.startTime > :now")
        List<Visite> findMyCreatedSlots(
                @Param("userId") Long userId,
                @Param("now") LocalDateTime now);

        @Query("SELECT v FROM Visite v " +
                "WHERE v.client.id = :userId " +
                "AND v.startTime > :now")
        List<Visite> findMyBookedSlots(
                @Param("userId") Long userId,
                @Param("now") LocalDateTime now);
    }