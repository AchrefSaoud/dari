package utm.tn.dari.modules.meuble.mapper;



import utm.tn.dari.entities.Meuble;
import utm.tn.dari.entities.User;
import utm.tn.dari.modules.meuble.dto.MeubleDTO;

public class MeubleMapper {

    public static MeubleDTO toDto(Meuble meuble) {
        MeubleDTO dto = new MeubleDTO();
        dto.setId(meuble.getId());
        dto.setNom(meuble.getNom());
        dto.setDescription(meuble.getDescription());
        dto.setPrix(meuble.getPrix());
        dto.setPhotoUrl(meuble.getPhotoUrl());
        dto.setAdresse(meuble.getAdresse());
        dto.setVendeurId(meuble.getVendeur() != null ? meuble.getVendeur().getId() : null);
        return dto;
    }

    public static Meuble toEntity(MeubleDTO dto, User vendeur) {
        Meuble meuble = new Meuble();
        meuble.setNom(dto.getNom());
        meuble.setDescription(dto.getDescription());
        meuble.setPrix(dto.getPrix());
        meuble.setPhotoUrl(dto.getPhotoUrl());
        meuble.setAdresse(dto.getAdresse());
        meuble.setVendeur(vendeur);
        return meuble;
    }
}
