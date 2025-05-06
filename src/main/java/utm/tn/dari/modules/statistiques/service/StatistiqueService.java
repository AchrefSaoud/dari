package utm.tn.dari.modules.statistiques.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import utm.tn.dari.entities.Abonnement;
import utm.tn.dari.entities.User;
import utm.tn.dari.entities.enums.TypeAbonnement;
import utm.tn.dari.modules.statistiques.dto.AbonnementStatsDto;
import utm.tn.dari.modules.statistiques.dto.MonthlyStatsDto;
import utm.tn.dari.modules.abonnement.repositories.AbonnementRepository;
import utm.tn.dari.modules.authentication.repositories.UserRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatistiqueService {

    private final AbonnementRepository abonnementRepository;
    private final UserRepository userRepository;

    /**
     * Récupère les statistiques générales sur les abonnements
     * @return Les statistiques globales des abonnements
     */
    public AbonnementStatsDto getAbonnementStats() {
        log.info("Génération des statistiques des abonnements");
        List<Abonnement> allAbonnements = abonnementRepository.findAll();
        List<User> allUsers = userRepository.findAll();

        AbonnementStatsDto stats = new AbonnementStatsDto();

        // Nombre total d'abonnements
        stats.setTotalAbonnements(allAbonnements.size());

        // Abonnements actifs/inactifs (basé sur la date)
        LocalDate today = LocalDate.now();
        long activeAbonnements = allAbonnements.stream()
                .filter(a -> a.getDate() != null && !a.getDate().isBefore(today.minusMonths(1)))
                .count();

        stats.setActiveAbonnements((int) activeAbonnements);
        stats.setInactiveAbonnements(allAbonnements.size() - (int) activeAbonnements);

        // Répartition par type
        Map<TypeAbonnement, Integer> abonnementsByType = new HashMap<>();
        for (TypeAbonnement type : TypeAbonnement.values()) {
            int count = (int) allAbonnements.stream()
                    .filter(a -> a.getType() == type)
                    .count();
            abonnementsByType.put(type, count);
        }
        stats.setAbonnementsByType(abonnementsByType);

        // Prix moyen des abonnements
        if (!allAbonnements.isEmpty()) {
            BigDecimal totalPrice = allAbonnements.stream()
                    .map(Abonnement::getPrix)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            stats.setAveragePrice(totalPrice.divide(
                    BigDecimal.valueOf(allAbonnements.size()), 2, RoundingMode.HALF_UP));
        } else {
            stats.setAveragePrice(BigDecimal.ZERO);
        }

        // Type le plus populaire
        if (!allUsers.isEmpty() && !allAbonnements.isEmpty()) {
            Map<TypeAbonnement, Long> typeCount = allUsers.stream()
                    .filter(u -> u.getAbonnement() != null)
                    .collect(Collectors.groupingBy(
                            u -> u.getAbonnement().getType(),
                            Collectors.counting()));

            Optional<Map.Entry<TypeAbonnement, Long>> mostPopular =
                    typeCount.entrySet().stream()
                            .max(Map.Entry.comparingByValue());

            if (mostPopular.isPresent()) {
                stats.setMostPopularType(mostPopular.get().getKey());
            }
        }

        // Montant total généré
        BigDecimal totalRevenue = allUsers.stream()
                .filter(u -> u.getAbonnement() != null)
                .map(u -> u.getAbonnement().getPrix())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        stats.setTotalRevenue(totalRevenue);

        return stats;
    }

    /**
     * Récupère les statistiques d'abonnements par mois
     * @param year Année pour laquelle calculer les statistiques
     * @return Liste des statistiques mensuelles
     */
    public List<MonthlyStatsDto> getMonthlyStats(int year) {
        log.info("Génération des statistiques mensuelles pour l'année: {}", year);

        List<MonthlyStatsDto> monthlyStats = new ArrayList<>();
        LocalDate startDate = LocalDate.of(year, 1, 1);
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM", Locale.FRANCE);

        for (int month = 1; month <= 12; month++) {
            LocalDate currentMonth = startDate.withMonth(month);
            LocalDate nextMonth = month == 12
                    ? LocalDate.of(year + 1, 1, 1)
                    : LocalDate.of(year, month + 1, 1);

            // Trouver les abonnements créés dans ce mois
            List<Abonnement> monthlyAbonnements = abonnementRepository.findByDateBetween(
                    currentMonth, nextMonth.minusDays(1));

            // Créer le DTO pour ce mois
            MonthlyStatsDto dto = new MonthlyStatsDto();
            dto.setMonth(currentMonth.format(monthFormatter));
            dto.setYear(year);
            dto.setNewAbonnements(monthlyAbonnements.size());

            // Calculer le revenu généré ce mois
            BigDecimal monthlyRevenue = monthlyAbonnements.stream()
                    .map(Abonnement::getPrix)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            dto.setRevenue(monthlyRevenue);

            monthlyStats.add(dto);
        }

        return monthlyStats;
    }

    /**
     * Récupère le type d'abonnement le plus populaire parmi les utilisateurs
     * @return Le type d'abonnement le plus souscrit
     */
    public TypeAbonnement getMostPopularAbonnementType() {
        log.info("Recherche du type d'abonnement le plus populaire");
        List<User> allUsers = userRepository.findAll();

        if (allUsers.isEmpty()) {
            return null;
        }

        Map<TypeAbonnement, Long> typeCount = allUsers.stream()
                .filter(u -> u.getAbonnement() != null)
                .collect(Collectors.groupingBy(
                        u -> u.getAbonnement().getType(),
                        Collectors.counting()));

        if (typeCount.isEmpty()) {
            return null;
        }

        return typeCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    /**
     * Calcule le montant total généré par tous les abonnements
     * @return Le revenu total cumulé de tous les abonnements
     */
    public BigDecimal getTotalRevenue() {
        log.info("Calcul du montant total généré par les abonnements");
        List<User> allUsers = userRepository.findAll();

        return allUsers.stream()
                .filter(u -> u.getAbonnement() != null)
                .map(u -> u.getAbonnement().getPrix())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}