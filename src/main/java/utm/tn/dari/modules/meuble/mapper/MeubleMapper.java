package utm.tn.dari.modules.meuble.mapper;

import utm.tn.dari.entities.Meuble;
import utm.tn.dari.entities.Meuble;
import utm.tn.dari.entities.User;
import utm.tn.dari.modules.meuble.dto.MeubleCreateDTO;
import utm.tn.dari.modules.meuble.dto.MeubleDTO;

public class MeubleMapper {
    public static Meuble toEntity(MeubleCreateDTO dto, User vendeur) {
        Meuble meuble = new Meuble();
        meuble.setNom(dto.getNom());
        meuble.setDescription(dto.getDescription());
        meuble.setPrix(dto.getPrix());
        meuble.setAdresse(dto.getAdresse());
        meuble.setPhotoUrl(dto.getPhotoUrl());
        meuble.setVendeur(vendeur);
        return meuble;
    }

    public static void updateEntity(Meuble meuble, MeubleCreateDTO dto) {
        meuble.setNom(dto.getNom());
        meuble.setDescription(dto.getDescription());
        meuble.setPrix(dto.getPrix());
        meuble.setAdresse(dto.getAdresse());
        if (dto.getPhotoUrl() != null) {
            meuble.setPhotoUrl(dto.getPhotoUrl());
        }
    }

    public static MeubleDTO toDto(Meuble meuble) {
        MeubleDTO dto = new MeubleDTO();
        dto.setId(meuble.getId());
        dto.setNom(meuble.getNom());
        dto.setDescription(meuble.getDescription());
        dto.setPrix(meuble.getPrix());
        dto.setAdresse(meuble.getAdresse());
        dto.setPhotoUrl(meuble.getPhotoUrl());
        dto.setVendeurId(meuble.getVendeur().getId());
        dto.setVendeurUsername(meuble.getVendeur().getUsername());
        return dto;
    }
}
