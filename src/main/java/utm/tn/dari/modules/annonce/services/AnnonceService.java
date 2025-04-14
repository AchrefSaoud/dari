package utm.tn.dari.modules.annonce.services;

import io.jsonwebtoken.Claims;
import org.hibernate.AnnotationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Point;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import utm.tn.dari.entities.Abonnement;
import utm.tn.dari.entities.Annonce;
import utm.tn.dari.entities.User;
import utm.tn.dari.entities.enums.StatusAnnonce;
import utm.tn.dari.entities.enums.TypeAnnonce;
import utm.tn.dari.modules.annonce.Dtoes.AnnonceDTO;
import utm.tn.dari.modules.annonce.Dtoes.USearchQueryDTO;
import utm.tn.dari.modules.annonce.events.AnnoncePostedEvent;
import utm.tn.dari.modules.annonce.events.NewQueryEvent;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AnnonceService {


    @Autowired
    private AnnonceRepository annonceRepository;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private UserService userService;

    Logger logger = LoggerFactory.getLogger(AnnonceService.class);

    public AnnonceDTO postAnnonce(AnnonceDTO annonceDTO, List<MultipartFile> attachments) throws Exception {
        List<String> attachmentPaths = new ArrayList<>();

        try {
            org.springframework.security.core.userdetails.User userObject = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            String username = userObject.getUsername();
            User user = userService.findByUsername(username);
            // TODO : check if the user has a valid subscription


            if (user == null) {
                throw new ObjectNotFoundException("User not found");
            }

            Abonnement abonnement = user.getAbonnement();

        /*    if(abonnement == null) {
                throw new AnnonceServiceException("User has no subscription");
            }*/


            // TODO : create the annonce

            for (MultipartFile attachment : attachments) {
                String randomString = java.util.UUID.randomUUID().toString();
                try {
                    String filePath = saveFile(attachment, randomString);
                    attachmentPaths.add(filePath);

                }catch (Exception e){
                    if(e instanceof FileSavingException){
                        throw e;
                    }
                }

            }

            Annonce annonce = new Annonce();
            annonce.setTitre(annonceDTO.getTitre());
            annonce.setDescription(annonceDTO.getDescription());
            annonce.setPrix(annonceDTO.getPrix());
            annonce.setType(annonceDTO.getType());
            annonce.setStatus(annonceDTO.getStatus());
            annonce.setAttachmentPaths(attachmentPaths);
            annonce.setUser(user);
            annonce.setLatitude(annonceDTO.getLatitude());
            annonce.setLongitude(annonceDTO.getLongitude());

            annonce = this.annonceRepository.save(annonce);









            // TODO : trigger the notification to the users who are searching for alike annonce


            AnnonceDTO publishedAnnonceDTO = AnnonceDTO.builder()
                    .id(annonce.getId())
                    .type(annonce.getType())
                    .titre(annonce.getTitre())
                    .prix(annonce.getPrix())
                    .description(annonce.getDescription())
                    .longitude(annonce.getLongitude())
                    .latitude(annonce.getLatitude())
                    .status(annonce.getStatus())
                    .userId(user.getId())
                    .build();

            AnnoncePostedEvent annoncePostedEvent = new AnnoncePostedEvent(
                    this,
                    publishedAnnonceDTO
            );
            this.applicationEventPublisher.publishEvent(annoncePostedEvent);

           return publishedAnnonceDTO;
        }catch (Exception e){
            e.printStackTrace();
            deleteAttachmentFiles(attachmentPaths);



            if(e instanceof ObjectNotFoundException) {
                throw e;
            }
            if(e instanceof FileSavingException) {
                throw e;
            }
            throw new Exception("Error while creating the annonce");
        }
    }

    private String saveFile(MultipartFile multipartFile, String prefix) throws Exception {
        try {
            if (multipartFile.isEmpty()) {
                throw new FileSavingException("File is empty");
            }

            if (multipartFile.getSize() > 10 * 1024 * 1024) {
                throw new FileSavingException("File is too large");
            }

            String originalFilename = multipartFile.getOriginalFilename();
            if (originalFilename == null || originalFilename.trim().isEmpty()) {
                throw new FileSavingException("File name is null or empty");
            }
            if (originalFilename.length() > 255) {
                throw new FileSavingException("File name is too long");
            }
            if (multipartFile.getContentType() == null) {
                throw new FileSavingException("File type is null");
            }
            if(!multipartFile.getContentType().startsWith("image/") && !multipartFile.getContentType().startsWith("video/") && !multipartFile.getContentType().startsWith("application/pdf")) {
                throw new FileSavingException("File type is not supported, only images, videos and pdfs are allowed");
            }

            originalFilename = Paths.get(originalFilename).getFileName().toString(); // sanitize

            Path uploadPath = Paths.get("dari/uploads");
            Files.createDirectories(uploadPath);

            Path filePath = uploadPath.resolve(prefix + "_" + originalFilename);

            if (Files.exists(filePath)) {
                throw new Exception("File already exists");
            }

            Files.copy(multipartFile.getInputStream(), filePath);

            return filePath.toAbsolutePath().toString();

        } catch (Exception e) {
            if(e instanceof FileSavingException) {
                throw e;
            }
            throw new Exception("Error while saving the file", e);
        }
    }

    private void deleteFile(String filePath) throws Exception {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            } else {
                throw new Exception("File not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error while deleting the file");
        }
    }
    public AnnonceDTO updateAnnonce(Long annonceId,AnnonceDTO annonceDTO,List<MultipartFile> attachments) throws Exception {
       List<String> attachmentPaths = new ArrayList<>();
        try {
            org.springframework.security.core.userdetails.User userObject = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            String username = userObject.getUsername();






            Annonce annonce = annonceRepository.findById(annonceId).orElseThrow(() -> new ObjectNotFoundException("Annonce n'existe pas"));

            for (MultipartFile attachment : attachments) {
                String randomString = java.util.UUID.randomUUID().toString();
                String filePath = null;
                try {
                    filePath = saveFile(attachment, randomString);
                } catch (Exception e) {
                    if (e instanceof FileSavingException) {
                        throw  e;
                    }
                }
                attachmentPaths.add(filePath);
            }

            if(annonce.getUser().getUsername().equals(username)) {
                if(annonceDTO.getTitre() != null) {
                    annonce.setTitre(annonceDTO.getTitre());
                }
                if(annonceDTO.getDescription() != null) {
                    annonce.setDescription(annonceDTO.getDescription());
                }
                if(annonceDTO.getPrix() != 0) {
                    annonce.setPrix(annonceDTO.getPrix());
                }
                if(annonceDTO.getType() != null) {
                    annonce.setType(annonceDTO.getType());
                }
                if(annonceDTO.getStatus() != null) {
                    annonce.setStatus(annonceDTO.getStatus());
                }
                if(annonceDTO.getLatitude() != null && annonceDTO.getLongitude() != null) {
                    annonce.setLatitude(annonceDTO.getLatitude());
                    annonce.setLongitude(annonceDTO.getLongitude());
                }
                if (!attachmentPaths.isEmpty()) {
                    annonce.getAttachmentPaths().addAll(attachmentPaths);
                }
                annonce = annonceRepository.save(annonce);

                return AnnonceDTO.builder()
                        .userId(annonce.getUser().getId())
                        .status(annonce.getStatus())
                        .description(annonce.getDescription())
                        .type(annonce.getType())
                        .prix(annonce.getPrix())
                        .latitude(annonce.getLatitude())
                        .longitude(annonce.getLongitude())
                        .attachmentPaths(annonce.getAttachmentPaths())
                        .id(annonce.getId())
                        .titre(annonce.getTitre())
                        .build();



            } else {
                throw new UnthorizedActionException("Vous n'avez pas le droit de modifier cette annonce");
            }


        } catch (Exception e) {
            deleteAttachmentFiles(attachmentPaths);
            if(e instanceof ObjectNotFoundException) {
                throw e;
            }
            if(e instanceof FileSavingException) {
                throw e;
            }
            throw  new Exception("Error while updating the annonce");
        }
    }

    public void deleteAttachmentFiles(List<String> attachmentPaths){
        attachmentPaths.forEach(attachmentPath -> {
            try {
                deleteFile(attachmentPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public AnnonceDTO addAttachmentsToAnnoce(Long annonceId , List<MultipartFile> newAttachments) throws Exception{

        List<String> attachmentPaths = new ArrayList<>();
        try {

            org.springframework.security.core.userdetails.User userObject = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            String username = userObject.getUsername();

            Annonce annonce = annonceRepository.findById(annonceId).orElseThrow(() -> new ObjectNotFoundException("Annonce n'existe pas"));

            if(annonce.getUser().getUsername().equals(username)) {
                for (MultipartFile attachment : newAttachments) {
                    String randomString = java.util.UUID.randomUUID().toString();
                    String filePath = saveFile(attachment, randomString);
                    attachmentPaths.add(filePath);
                }
                annonce.getAttachmentPaths().addAll(attachmentPaths);
                annonce = annonceRepository.save(annonce);
                return AnnonceDTO.builder()
                        .userId(annonce.getUser().getId())
                        .status(annonce.getStatus())
                        .description(annonce.getDescription())
                        .type(annonce.getType())
                        .prix(annonce.getPrix())
                        .titre(annonce.getTitre())
                        .build();
            }
            else {
                throw new UnthorizedActionException("Vous n'avez pas le droit de modifier cette annonce");
            }

        }catch (Exception e){

            attachmentPaths.forEach(attachmentPath -> {
                try {
                    deleteFile(attachmentPath);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            if(e instanceof ObjectNotFoundException) {
                throw e;
            }
            if(e instanceof FileSavingException) {
                throw e;
            }
            throw new Exception("Error while adding the attachments to the annonce");
        }
    }

    private List<File> getAttachmentsOfAnnonce(Long annonceId) throws Exception {
        try {
            Annonce annonce = annonceRepository.findById(annonceId).orElseThrow(() -> new ObjectNotFoundException("Annonce n'existe pas"));
            List<File> files = new ArrayList<>();
            for (String attachmentPath : annonce.getAttachmentPaths()) {
                File file = new File(attachmentPath);
                if (file.exists()) {
                    files.add(file);
                } else {
                    // TODO : what to do here
                }
            }
            return files;
        } catch (Exception e) {
            if(e instanceof ObjectNotFoundException) {
                throw e;
            }
            throw new Exception("Error while getting the attachments of the annonce");
        }
    }

  public Page<AnnonceDTO> getMyAnnonces (  TypeAnnonce type,
                                           StatusAnnonce status,
                                           String description,
                                           String titre,
                                            Float minPrice,
                                            Float maxPrice,
                                           Double longitude,
                                           Double latitude,
                                           Double radius,
                                           int pageNumber,

                                           int pageSize) throws Exception{
        try {

            return getQueriedAnnonces(
                    titre,
                    description,
                    type,
                    status,
                    getUsernameFromToken(),
                    minPrice,
                    maxPrice,
                    longitude,
                    latitude,
                    radius,
                    Math.toIntExact(pageNumber),
                    Math.toIntExact(pageSize)
            );
        }catch (Exception e){
            throw new Exception("Error while getting the annonces");
        }
  }
  public Page<AnnonceDTO> getQueriedAnnonces(

            String titre,
            String description,
            TypeAnnonce type,
            StatusAnnonce status,
            String searchedUsername,
            Float minPrice,
            Float maxPrice,
            Double longitude,
            Double latitude,
            Double radius,
            int pageNumber,
            int pageSize
    ) throws Exception {
        try {
            org.springframework.security.core.userdetails.User userObject = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            User user = this.userService.findByUsername(userObject.getUsername());

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

            if (latitude != null && longitude != null && radius != null) {
                spec = spec.and(AnnonceSearchSpecification.filterByGeolocalisation(latitude, longitude, radius));
            }

            if(minPrice == null){
                minPrice = 0f;
            }
            // heighest float
            if(maxPrice == null){
                maxPrice = 3.4028234e30f;
            }
                spec = spec.and(AnnonceSearchSpecification.filterByPriceRange(minPrice, maxPrice));



            if (titre != null && !titre.isEmpty()) {
                spec = spec.and(AnnonceSearchSpecification.filterByTitle(titre));
            }
            if (description != null && !description.isEmpty()) {
                spec = spec.and(AnnonceSearchSpecification.filterByDescription(description));
            }





            Pageable pageable = Pageable.ofSize(pageSize).withPage(pageNumber);
            Page<Annonce> annonces = annonceRepository.findAll(spec, pageable);
            List<AnnonceDTO> annonceDTOs = new ArrayList<>();
            for (Annonce annonce : annonces) {
                AnnonceDTO annonceDTO = AnnonceDTO.builder()
                        .id(annonce.getId())
                        .titre(annonce.getTitre())
                        .description(annonce.getDescription())
                        .prix(annonce.getPrix())
                        .type(annonce.getType())
                        .status(annonce.getStatus())
                        .userId(annonce.getUser().getId())
                        .latitude(annonce.getLatitude())
                        .longitude(annonce.getLongitude())
                        .attachmentPaths(annonce.getAttachmentPaths())
                        .build();
                annonceDTOs.add(annonceDTO);
            }


            NewQueryEvent newQueryEvent =  new NewQueryEvent(
                    this,
                    USearchQueryDTO.builder()
                            .description(description)
                            .titre(titre)
                            .minPrix(minPrice)
                            .maxPrix(maxPrice)
                            .type(type)
                            .statusAnnonce(status)
                            .latitude(latitude)
                            .longitude(longitude)
                            .radius(radius)
                            .createdAt(java.time.LocalDateTime.now())
                            .userId(user.getId())
                            .build()
            );

            this.applicationEventPublisher.publishEvent(newQueryEvent);

            PageImpl <AnnonceDTO> annonceDTOsPage = new PageImpl<>(annonceDTOs, pageable, annonces.getTotalElements());
            return annonceDTOsPage;
        } catch (Exception e) {
            e.printStackTrace();

            throw new Exception("Error while getting the annonces");
        }
    }

    public String getUsernameFromToken() {
        Claims claims = (Claims) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return claims.getSubject();
    }

    public Boolean deleteAnnonce(Long annonceId) throws Exception {
        try {
            Annonce annonce = annonceRepository.findById(annonceId).orElseThrow(() -> new ObjectNotFoundException("Annonce n'existe pas"));
            org.springframework.security.core.userdetails.User userObject = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            String username = userObject.getUsername();
            if(annonce.getUser().getUsername().equals(username)) {
                annonceRepository.delete(annonce);
            } else {
                throw new UnthorizedActionException("Vous n'avez pas le droit de supprimer cette annonce");
            }
            return true;
        } catch (Exception e) {
            if(e instanceof ObjectNotFoundException) {
                throw e;
            }
            throw new Exception("Error while deleting the annonce");
        }
    }

    public AnnonceDTO getAnnonceDTOById(Long annonceId) throws Exception {
        try {
            Optional<Annonce> annonceOptional = annonceRepository.findById(annonceId);

            if (annonceOptional.isEmpty()) {
               throw new ObjectNotFoundException("Annonce n'existe pas");
            }
            Annonce annonce = annonceOptional.get();
            return AnnonceDTO.builder()
                    .id(annonce.getId())
                    .titre(annonce.getTitre())
                    .description(annonce.getDescription())
                    .prix(annonce.getPrix())
                    .type(annonce.getType())
                    .status(annonce.getStatus())
                    .userId(annonce.getUser().getId())
                    .build();
        } catch (Exception e) {
           throw new Exception("Error while getting the annonce");
        }
    }
    public AnnonceDTO getAnnonceById(Long annonceId) throws Exception {
        try {
            Optional<Annonce> annonceOptional = annonceRepository.findById(annonceId);

            Annonce annonce = annonceOptional
                    .orElseThrow(() -> new ObjectNotFoundException("Annonce n'existe pas"));
            return AnnonceDTO.builder()
                    .titre(annonce.getTitre())
                    .prix(annonce.getPrix())
                    .type(annonce.getType())
                    .id(annonce.getId())
                    .description(annonce.getDescription())
                    .status(annonce.getStatus())
                    .latitude(annonce.getLatitude())
                    .longitude(annonce.getLongitude())
                    .userId(annonce.getUser().getId())
                    .attachmentPaths(annonce.getAttachmentPaths())
                    .build();
        } catch (Exception e) {
            if(e instanceof ObjectNotFoundException) {
                throw e;
            }
            throw new Exception("Error while getting the annonce");
        }
    }
    public Annonce getAnnonceObjById(Long annonceId) throws Exception {
        try {
            Optional<Annonce> annonceOptional = annonceRepository.findById(annonceId);

            Annonce annonce = annonceOptional
                    .orElseThrow(() -> new ObjectNotFoundException("Annonce n'existe pas"));
           return annonce;
        } catch (Exception e) {
            if(e instanceof ObjectNotFoundException) {
                throw e;
            }
            throw new Exception("Error while getting the annonce");
        }
    }
    public Page<AnnonceDTO> getAnnoncesByUserId(String username,int pageNumber , int pageSize) throws Exception {
        try {
            User user = userService.findByUsername(username);
            if (user == null) {
                throw new ObjectNotFoundException("User not found");
            }
            Pageable pageable = Pageable.ofSize(pageSize).withPage(pageNumber);
            Page<Annonce> annonces = annonceRepository.findAllByUser(user,pageable);
            List<AnnonceDTO> annonceDTOs = new ArrayList<>();
            for (Annonce annonce : annonces) {
                AnnonceDTO annonceDTO = AnnonceDTO.builder()
                        .id(annonce.getId())
                        .titre(annonce.getTitre())
                        .description(annonce.getDescription())
                        .prix(annonce.getPrix())
                        .type(annonce.getType())
                        .status(annonce.getStatus())
                        .userId(annonce.getUser().getId())
                        .build();
                annonceDTOs.add(annonceDTO);
            }
            return new PageImpl<>(annonceDTOs, pageable, annonces.getTotalElements());
        } catch (Exception e) {
            if(e instanceof ObjectNotFoundException) {
                throw e;
            }
            throw new Exception("Error while getting the annonces");
        }
    }
}
