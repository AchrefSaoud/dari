package utm.tn.dari.modules.statistiques.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import utm.tn.dari.entities.Annonce;
import utm.tn.dari.entities.User;
import utm.tn.dari.entities.enums.StatusAnnonce;
import utm.tn.dari.entities.enums.TypeAnnonce;
import utm.tn.dari.modules.annonce.exceptions.ObjectNotFoundException;
import utm.tn.dari.modules.annonce.exceptions.UnthorizedActionException;
import utm.tn.dari.modules.annonce.repositories.AnnonceRepository;
import utm.tn.dari.modules.authentication.repositories.UserRepository;
import utm.tn.dari.modules.statistiques.dto.statistics.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnnonceStatisticsService {
    private final AnnonceRepository annonceRepository;
    private final UserRepository userRepository;

    public GeneralStatisticsDTO getGeneralStatistics() {
        List<Annonce> announcements = annonceRepository.findAll();
        return GeneralStatisticsDTO.builder()
                .totalAnnouncements(announcements.size())
                .averagePrice(calculateAveragePrice(announcements))
                .statusCounts(countByStatus(announcements))
                .lastUpdateDate(LocalDate.now())
                .build();
    }

    public TypeStatisticsDTO getStatisticsByType() {
        List<Annonce> announcements = annonceRepository.findAll();
        return TypeStatisticsDTO.builder()
                .distributionByType(countByType(announcements))
                .averagePriceByType(calculateAveragePriceByType(announcements))
                .totalAnnouncements(announcements.size())
                .build();
    }

    public StatusStatisticsDTO getStatisticsByStatus() {
        List<Annonce> announcements = annonceRepository.findAll();
        Map<StatusAnnonce, Long> distribution = countByStatus(announcements);
        return StatusStatisticsDTO.builder()
                .distributionByStatus(distribution)
                .percentageByStatus(calculateStatusPercentages(distribution, announcements.size()))
                .totalAnnouncements(announcements.size())
                .build();
    }

    public PriceStatisticsDTO getPriceStatisticsByType(TypeAnnonce type) {
        List<Annonce> announcements = annonceRepository.findAll().stream()
                .filter(a -> a.getTypeAnnonce() == type)
                .collect(Collectors.toList());

        if (announcements.isEmpty()) {
            throw new ObjectNotFoundException("No announcements found for type: " + type);
        }

        List<Float> prices = announcements.stream()
                .map(Annonce::getPrice)
                .collect(Collectors.toList());

        return PriceStatisticsDTO.builder()
                .type(type)
                .minimumPrice(Collections.min(prices))
                .maximumPrice(Collections.max(prices))
                .averagePrice(calculateAverage(prices))
                .medianPrice(calculateMedian(prices))
                .priceRanges(generatePriceRanges(prices))
                .build();
    }

    public GeographicStatisticsDTO getGeographicStatistics() {
        List<Annonce> announcements = annonceRepository.findAll();
        return GeographicStatisticsDTO.builder()
                .regionStatistics(getRegionsStatistics(announcements))
                .totalAnnouncements(announcements.size())
                .build();
    }

    public TimeSeriesStatisticsDTO getTimeSeriesStatistics(LocalDate startDate, LocalDate endDate, TimeInterval interval) {
        List<Annonce> announcements = annonceRepository.findAll().stream()
                .filter(a -> isAnnounceBetweenDates(a, startDate, endDate))
                .collect(Collectors.toList());

        return TimeSeriesStatisticsDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .interval(interval)
                .announcementCounts(groupAnnouncementsByInterval(announcements, startDate, endDate, interval))
                .build();
    }

    public UserStatisticsDTO getUserStatistics() throws UnthorizedActionException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new UnthorizedActionException("User not authenticated");
        }

        List<Annonce> userAnnouncements = annonceRepository.findAll().stream()
                .filter(annonce -> annonce.getUser().getEmail().equals(auth.getName()))
                .collect(Collectors.toList());

        return UserStatisticsDTO.builder()
                .totalUserAnnouncements(userAnnouncements.size())
                .distributionByType(countByType(userAnnouncements))
                .distributionByStatus(countByStatus(userAnnouncements))
                .averagePrice(calculateAveragePrice(userAnnouncements))
                .build();
    }

    private double calculateAveragePrice(List<Annonce> announcements) {
        if (announcements.isEmpty()) {
            return 0.0;
        }
        return announcements.stream()
                .mapToDouble(Annonce::getPrice)
                .average()
                .orElse(0.0);
    }

    private Map<TypeAnnonce, Double> calculateAveragePriceByType(List<Annonce> announcements) {
        return announcements.stream()
                .collect(Collectors.groupingBy(
                        Annonce::getTypeAnnonce,
                        Collectors.averagingDouble(Annonce::getPrice)
                ));
    }

    private Map<StatusAnnonce, Long> countByStatus(List<Annonce> announcements) {
        return announcements.stream()
                .collect(Collectors.groupingBy(
                        Annonce::getStatus,
                        Collectors.counting()
                ));
    }

    private Map<TypeAnnonce, Long> countByType(List<Annonce> announcements) {
        return announcements.stream()
                .collect(Collectors.groupingBy(
                        Annonce::getTypeAnnonce,
                        Collectors.counting()
                ));
    }

    private Map<StatusAnnonce, Double> calculateStatusPercentages(Map<StatusAnnonce, Long> distribution, long total) {
        if (total == 0) {
            return new HashMap<>();
        }
        return distribution.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> (e.getValue() * 100.0) / total
                ));
    }

    private double calculateMedian(List<Float> values) {
        if (values.isEmpty()) {
            return 0.0;
        }
        List<Float> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        int middle = sorted.size() / 2;
        if (sorted.size() % 2 == 0) {
            return (sorted.get(middle - 1) + sorted.get(middle)) / 2.0;
        }
        return sorted.get(middle);
    }

    private double calculateAverage(List<Float> values) {
        if (values.isEmpty()) {
            return 0.0;
        }
        return values.stream()
                .mapToDouble(Float::doubleValue)
                .average()
                .orElse(0.0);
    }

    private List<PriceRangeDTO> generatePriceRanges(List<Float> prices) {
        if (prices.isEmpty()) {
            return Collections.emptyList();
        }

        float min = Collections.min(prices);
        float max = Collections.max(prices);
        float range = (max - min) / 5.0f;

        List<PriceRangeDTO> ranges = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            float rangeMin = min + (range * i);
            float rangeMax = rangeMin + range;
            long count = prices.stream()
                    .filter(price -> price >= rangeMin && price < rangeMax)
                    .count();

            ranges.add(PriceRangeDTO.builder()
                    .minPrice(rangeMin)
                    .maxPrice(rangeMax)
                    .count(count)
                    .percentage((count * 100.0) / prices.size())
                    .build());
        }
        return ranges;
    }

    private List<RegionStatisticsDTO> getRegionsStatistics(List<Annonce> announcements) {
        Map<String, List<Annonce>> byRegion = announcements.stream()
                .collect(Collectors.groupingBy(Annonce::getRegion));

        return byRegion.entrySet().stream()
                .map(entry -> RegionStatisticsDTO.builder()
                        .region(entry.getKey())
                        .announcementCount(entry.getValue().size())
                        .averagePrice(calculateAveragePrice(entry.getValue()))
                        .build())
                .collect(Collectors.toList());
    }

    private boolean isAnnounceBetweenDates(Annonce annonce, LocalDate startDate, LocalDate endDate) {
        LocalDate announceDate = annonce.getCreatedAt().toLocalDate();
        return !announceDate.isBefore(startDate) && !announceDate.isAfter(endDate);
    }

    private Map<LocalDate, Long> groupAnnouncementsByInterval(
            List<Annonce> announcements,
            LocalDate startDate,
            LocalDate endDate,
            TimeInterval interval
    ) {
        Map<LocalDate, Long> result = new TreeMap<>();
        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {
            LocalDate intervalEnd = getIntervalEnd(current, interval);
            if (intervalEnd.isAfter(endDate)) {
                intervalEnd = endDate;
            }

            LocalDate finalCurrent = current;
            LocalDate finalIntervalEnd = intervalEnd;

            long count = announcements.stream()
                    .filter(a -> isAnnounceBetweenDates(a, finalCurrent, finalIntervalEnd))
                    .count();

            result.put(current, count);
            current = intervalEnd.plusDays(1);
        }

        return result;
    }

    private LocalDate getIntervalEnd(LocalDate start, TimeInterval interval) {
        return switch (interval) {
            case DAY -> start;
            case WEEK -> start.plusWeeks(1).minusDays(1);
            case MONTH -> start.plusMonths(1).minusDays(1);
            case YEAR -> start.plusYears(1).minusDays(1);
        };
    }
}