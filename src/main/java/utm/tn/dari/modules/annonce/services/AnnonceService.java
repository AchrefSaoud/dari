package utm.tn.dari.modules.annonce.services;

import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import utm.tn.dari.entities.Annonce;
import utm.tn.dari.entities.User;
import utm.tn.dari.entities.enums.*;
import utm.tn.dari.modules.annonce.Dtoes.AnnonceDTO;
import utm.tn.dari.modules.annonce.Dtoes.USearchQueryDTO;
import utm.tn.dari.modules.annonce.Utils.Haversine;
import utm.tn.dari.modules.annonce.Utils.MultipartFileCompressor;
import utm.tn.dari.modules.annonce.events.AnnoncePostedEvent;
import utm.tn.dari.modules.annonce.events.NewQueryEvent;
import utm.tn.dari.modules.annonce.events.PriceChangedEvent;
import utm.tn.dari.modules.annonce.exceptions.FileSavingException;
import utm.tn.dari.modules.annonce.exceptions.ObjectNotFoundException;
import utm.tn.dari.modules.annonce.exceptions.UnthorizedActionException;
import utm.tn.dari.modules.annonce.repositories.AnnonceRepository;
import utm.tn.dari.modules.annonce.repositories.AnnonceSearchSpecification;
import utm.tn.dari.security.services.UserService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnnonceService {

    private static final Logger logger = LoggerFactory.getLogger(AnnonceService.class);
    private static final String DIR_PATH = "dari/uploads";
    private static final float MAX_FILE_SIZE_MB = 10;
    private static final float IMAGE_COMPRESSION_QUALITY = 0.7f;

    @Autowired
    private AnnonceRepository annonceRepository;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private UserService userService;

    public AnnonceDTO postAnnonce(AnnonceDTO annonceDTO, List<MultipartFile> attachments) throws Exception {
        List<String> attachmentPaths = processAttachments(attachments);

        try {
            User user = getAuthenticatedUser();
            validateAnnonceDTO(annonceDTO);

            Annonce annonce = buildAnnonceFromDTO(annonceDTO, user, attachmentPaths);
            annonce = annonceRepository.save(annonce);

            AnnonceDTO publishedAnnonceDTO = buildAnnonceDTO(annonce);
            publishAnnoncePostedEvent(publishedAnnonceDTO);

            return publishedAnnonceDTO;
        } catch (Exception e) {
            deleteAttachmentFiles(attachmentPaths);
            throw e;
        }
    }

    public AnnonceDTO updateAnnonce(Long annonceId, AnnonceDTO annonceDTO, List<MultipartFile> attachments) throws Exception {
        List<String> attachmentPaths = processAttachments(attachments);

        try {
            String username = getAuthenticatedUsername();
            Annonce annonce = getAnnonceByIdOrThrow(annonceId);

            if (!annonce.getUser().getUsername().equals(username)) {
                throw new UnthorizedActionException("Vous n'avez pas le droit de modifier cette annonce");
            }

            float oldPrice = annonce.getPrix();
            updateAnnonceFromDTO(annonce, annonceDTO, attachmentPaths);
            annonce = annonceRepository.save(annonce);

            AnnonceDTO updatedAnnonceDTO = buildAnnonceDTO(annonce);

            if (annonceDTO.getPrix() != 0 && annonceDTO.getPrix() != oldPrice) {
                publishPriceChangedEvent(updatedAnnonceDTO, oldPrice, annonceDTO.getPrix());
            }

            return updatedAnnonceDTO;
        } catch (Exception e) {
            deleteAttachmentFiles(attachmentPaths);
            throw e;
        }
    }

    public AnnonceDTO addAttachmentsToAnnonce(Long annonceId, List<MultipartFile> newAttachments) throws Exception {
        List<String> attachmentPaths = processAttachments(newAttachments);

        try {
            String username = getAuthenticatedUsername();
            Annonce annonce = getAnnonceByIdOrThrow(annonceId);

            if (!annonce.getUser().getUsername().equals(username)) {
                throw new UnthorizedActionException("Vous n'avez pas le droit de modifier cette annonce");
            }

            annonce.getAttachmentPaths().addAll(attachmentPaths);
            annonce = annonceRepository.save(annonce);

            return buildAnnonceDTO(annonce);
        } catch (Exception e) {
            deleteAttachmentFiles(attachmentPaths);
            throw e;
        }
    }

    public Page<AnnonceDTO> getMyAnnonces(TypeAnnonce type, StatusAnnonce status, String query,
                                          Float minPrice, Float maxPrice, TypeBien typeBien,
                                          Rooms rooms, LeaseDuration leaseDuration,
                                          Double longitude, Double latitude, Double radius,
                                          int pageNumber, int pageSize) throws Exception {
        return getQueriedAnnonces(query, type, status, getAuthenticatedUsername(),
                minPrice, maxPrice, typeBien, rooms, leaseDuration,
                longitude, latitude, radius, pageNumber, pageSize);
    }

    public Page<AnnonceDTO> getQueriedAnnonces(String query, TypeAnnonce type, StatusAnnonce status,
                                               String searchedUsername, Float minPrice, Float maxPrice,
                                               TypeBien typeBien, Rooms rooms, LeaseDuration leaseDuration,
                                               Double latitude, Double longitude, Double radius,
                                               int pageNumber, int pageSize) throws Exception {
        try {
            User user = getAuthenticatedUser();
            Specification<Annonce> spec = buildSpecification(query, type, status, searchedUsername,
                    minPrice, maxPrice, typeBien, rooms, leaseDuration);

            Pageable pageable = Pageable.ofSize(pageSize).withPage(pageNumber);
            Page<Annonce> annonces = annonceRepository.findAll(spec, pageable);

            List<AnnonceDTO> filteredAnnonces = filterAnnoncesByLocation(annonces, latitude, longitude, radius)
                    .stream()
                    .map(this::buildAnnonceDTO)
                    .collect(Collectors.toList());

            publishNewQueryEvent(user, query, type, status, minPrice, maxPrice, latitude, longitude, radius,rooms);

            return new PageImpl<>(filteredAnnonces, pageable, annonces.getTotalElements());
        } catch (Exception e) {
            logger.error("Error while getting the annonces", e);
            throw new Exception("Error while getting the annonces");
        }
    }

    public Boolean deleteAnnonce(Long annonceId) throws Exception {
        try {
            Annonce annonce = getAnnonceByIdOrThrow(annonceId);
            String username = getAuthenticatedUsername();

            if (!annonce.getUser().getUsername().equals(username)) {
                throw new UnthorizedActionException("Vous n'avez pas le droit de supprimer cette annonce");
            }

            annonceRepository.delete(annonce);
            return true;
        } catch (Exception e) {
            throw e;
        }
    }

    public AnnonceDTO getAnnonceDTOById(Long annonceId) throws Exception {
        Annonce annonce = getAnnonceByIdOrThrow(annonceId);
        return buildBasicAnnonceDTO(annonce);
    }

    public AnnonceDTO getAnnonceById(Long annonceId) throws Exception {
        Annonce annonce = getAnnonceByIdOrThrow(annonceId);
        return buildAnnonceDTO(annonce);
    }

    public Annonce getAnnonceObjById(Long annonceId) throws Exception {
        return getAnnonceByIdOrThrow(annonceId);
    }

    public Page<AnnonceDTO> getAnnoncesByUserId(String username, int pageNumber, int pageSize) throws Exception {
        try {
            User user = userService.findByUsername(username);
            if (user == null) {
                throw new ObjectNotFoundException("User not found");
            }

            Pageable pageable = Pageable.ofSize(pageSize).withPage(pageNumber);
            Page<Annonce> annonces = annonceRepository.findAllByUser(user, pageable);

            return annonces.map(this::buildBasicAnnonceDTO);
        } catch (Exception e) {
            throw e;
        }
    }

    private List<String> processAttachments(List<MultipartFile> attachments) throws Exception {
        List<String> attachmentPaths = new ArrayList<>();

        for (MultipartFile attachment : attachments) {
            String randomString = UUID.randomUUID().toString();
            String filePath;

            try {
                if (isImageFile(attachment)) {
                    filePath = MultipartFileCompressor.compressThenSaveMultipartFile(
                            attachment, DIR_PATH, randomString, IMAGE_COMPRESSION_QUALITY);
                } else {
                    filePath = saveFile(attachment, randomString);
                }

                if (filePath != null && !filePath.isEmpty()) {
                    attachmentPaths.add(randomString + "_" + attachment.getOriginalFilename());
                }
            } catch (FileSavingException e) {
                throw e;
            } catch (Exception e) {
                logger.error("Error processing attachment", e);
            }
        }

        return attachmentPaths;
    }

    private String saveFile(MultipartFile multipartFile, String prefix) throws Exception {
        if (multipartFile.isEmpty()) {
            throw new FileSavingException("File is empty");
        }

        if (multipartFile.getSize() > MAX_FILE_SIZE_MB * 1024 * 1024) {
            throw new FileSavingException("File is too large");
        }

        String originalFilename = validateFilename(multipartFile.getOriginalFilename());
        validateContentType(multipartFile.getContentType());

        Path uploadPath = Paths.get(DIR_PATH);
        Files.createDirectories(uploadPath);

        String relativeFilePath = prefix + "_" + originalFilename;
        Path filePath = uploadPath.resolve(relativeFilePath);

        if (Files.exists(filePath)) {
            throw new FileSavingException("File already exists");
        }

        Files.copy(multipartFile.getInputStream(), filePath);
        return relativeFilePath;
    }

    private void deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            logger.error("Error deleting file", e);
        }
    }

    private void deleteAttachmentFiles(List<String> attachmentPaths) {
        attachmentPaths.forEach(this::deleteFile);
    }

    private User getAuthenticatedUser() throws ObjectNotFoundException {
        String username = getAuthenticatedUsername();
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new ObjectNotFoundException("User not found");
        }
        return user;
    }

    private String getAuthenticatedUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private Annonce getAnnonceByIdOrThrow(Long annonceId) throws ObjectNotFoundException {
        return annonceRepository.findById(annonceId)
                .orElseThrow(() -> new ObjectNotFoundException("Annonce n'existe pas"));
    }

    private void validateAnnonceDTO(AnnonceDTO annonceDTO) throws ObjectNotFoundException {
        if (annonceDTO.getType().equals(TypeAnnonce.LOCATION) && annonceDTO.getLeaseDuration() == null) {
            throw new ObjectNotFoundException("Lease duration cannot be null");
        }
        if (annonceDTO.getType().equals(TypeAnnonce.VENTE) && annonceDTO.getLeaseDuration() != null) {
            throw new ObjectNotFoundException("Lease duration cannot be assigned");
        }
    }

    private Annonce buildAnnonceFromDTO(AnnonceDTO annonceDTO, User user, List<String> attachmentPaths) {
        return Annonce.builder()
                .titre(annonceDTO.getTitre())
                .description(annonceDTO.getDescription())
                .prix(annonceDTO.getPrix())
                .leaseDuration(annonceDTO.getLeaseDuration())
                .type(annonceDTO.getType())
                .typeBien(annonceDTO.getTypeBien())
                .rooms(annonceDTO.getRooms())
                .status(StatusAnnonce.EN_ATTENTE)
                .attachmentPaths(attachmentPaths)
                .user(user)
                .latitude(annonceDTO.getLatitude())
                .longitude(annonceDTO.getLongitude())
                .build();
    }

    private void updateAnnonceFromDTO(Annonce annonce, AnnonceDTO annonceDTO, List<String> attachmentPaths) {
        Optional.ofNullable(annonceDTO.getTitre()).ifPresent(annonce::setTitre);
        Optional.ofNullable(annonceDTO.getDescription()).ifPresent(annonce::setDescription);
        Optional.ofNullable(annonceDTO.getPrix()).ifPresent(annonce::setPrix);
        Optional.ofNullable(annonceDTO.getType()).ifPresent(annonce::setType);
        Optional.ofNullable(annonceDTO.getStatus()).ifPresent(annonce::setStatus);
        Optional.ofNullable(annonceDTO.getTypeBien()).ifPresent(annonce::setTypeBien);

        if (annonceDTO.getLatitude() != null && annonceDTO.getLongitude() != null) {
            annonce.setLatitude(annonceDTO.getLatitude());
            annonce.setLongitude(annonceDTO.getLongitude());
        }

        if (annonceDTO.getLeaseDuration() != null && annonce.getType().equals(TypeAnnonce.LOCATION)) {
            annonce.setLeaseDuration(annonceDTO.getLeaseDuration());
        }

        if (!attachmentPaths.isEmpty()) {
            annonce.getAttachmentPaths().addAll(attachmentPaths);
        }
    }

    private AnnonceDTO buildAnnonceDTO(Annonce annonce) {
        return AnnonceDTO.builder()
                .id(annonce.getId())
                .titre(annonce.getTitre())
                .description(annonce.getDescription())
                .prix(annonce.getPrix())
                .type(annonce.getType())
                .status(annonce.getStatus())
                .rooms(annonce.getRooms())
                .leaseDuration(annonce.getLeaseDuration())
                .userId(annonce.getUser().getId())
                .typeBien(annonce.getTypeBien())
                .latitude(annonce.getLatitude())
                .longitude(annonce.getLongitude())
                .attachmentPaths(annonce.getAttachmentPaths())
                .build();
    }

    private AnnonceDTO buildBasicAnnonceDTO(Annonce annonce) {
        return AnnonceDTO.builder()
                .id(annonce.getId())
                .titre(annonce.getTitre())
                .description(annonce.getDescription())
                .prix(annonce.getPrix())
                .type(annonce.getType())
                .status(annonce.getStatus())
                .userId(annonce.getUser().getId())
                .build();
    }

    private Specification<Annonce> buildSpecification(String query, TypeAnnonce type, StatusAnnonce status,
                                                      String searchedUsername, Float minPrice, Float maxPrice,
                                                      TypeBien typeBien, Rooms rooms, LeaseDuration leaseDuration) {
        Specification<Annonce> spec = Specification.where(null);

        if (searchedUsername != null && !searchedUsername.isEmpty()) {
            spec = spec.and(AnnonceSearchSpecification.filterByUsername(searchedUsername));
        }
        if (type != null) {
            spec = spec.and(AnnonceSearchSpecification.filterByType(type));
        }
        if (status != null) {
            spec = spec.and(AnnonceSearchSpecification.filterByStatus(status));
        }
        if (leaseDuration != null) {
            spec = spec.and(AnnonceSearchSpecification.filterByLeaseDuration(leaseDuration));
        }

        float effectiveMinPrice = minPrice != null ? minPrice : 0f;
        float effectiveMaxPrice = maxPrice != null ? maxPrice : Float.MAX_VALUE;
        spec = spec.and(AnnonceSearchSpecification.filterByPriceRange(effectiveMinPrice, effectiveMaxPrice));

        if (typeBien != null) {
            spec = spec.and(AnnonceSearchSpecification.filterByTypeBien(typeBien));
        }
        if (query != null) {
            spec = spec.and(AnnonceSearchSpecification.filterByDescription(query));
        }
        if (rooms != null && !rooms.equals(Rooms.ANY)) {
            spec = spec.and(AnnonceSearchSpecification.filterByRooms(rooms));
        }

        return spec;
    }

    private List<Annonce> filterAnnoncesByLocation(Page<Annonce> annonces, Double latitude, Double longitude, Double radius) {
        if (latitude == null || longitude == null || radius == null) {
            return annonces.getContent();
        }

        return annonces.getContent().stream()
                .filter(a -> radius >= Haversine.distance(a.getLatitude(), a.getLongitude(), latitude, longitude))
                .collect(Collectors.toList());
    }

    private void publishAnnoncePostedEvent(AnnonceDTO annonceDTO) {
        applicationEventPublisher.publishEvent(new AnnoncePostedEvent(this, annonceDTO));
    }

    private void publishPriceChangedEvent(AnnonceDTO annonceDTO, float oldPrice, float newPrice) {
        applicationEventPublisher.publishEvent(new PriceChangedEvent(this, annonceDTO, oldPrice, newPrice));
    }

    private void publishNewQueryEvent(User user, String query, TypeAnnonce type, StatusAnnonce status,
                                      Float minPrice, Float maxPrice, Double latitude, Double longitude, Double radius,Rooms rooms) {
        USearchQueryDTO searchQuery = USearchQueryDTO.builder()
                .query(query)
                .minPrix(minPrice)
                .maxPrix(maxPrice)
                .type(type)
                .statusAnnonce(status)
                .latitude(latitude)
                .rooms(rooms)
                .longitude(longitude)
                .radius(radius)
                .createdAt(java.time.LocalDateTime.now())
                .userId(user.getId())
                .build();

        applicationEventPublisher.publishEvent(new NewQueryEvent(this, searchQuery));
    }

    private boolean isImageFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        return filename != null &&
                (filename.endsWith(".jpg") ||
                        filename.endsWith(".jpeg") ||
                        filename.endsWith(".png"));
    }

    private String validateFilename(String filename) throws FileSavingException {
        if (filename == null || filename.trim().isEmpty()) {
            throw new FileSavingException("File name is null or empty");
        }
        if (filename.length() > 255) {
            throw new FileSavingException("File name is too long");
        }
        return Paths.get(filename).getFileName().toString();
    }

    private void validateContentType(String contentType) throws FileSavingException {
        if (contentType == null) {
            throw new FileSavingException("File type is null");
        }
        if (!contentType.startsWith("image/") &&
                !contentType.startsWith("video/") &&
                !contentType.startsWith("application/pdf")) {
            throw new FileSavingException("File type is not supported, only images, videos and pdfs are allowed");
        }
    }
}