package utm.tn.dari.modules.abonnement.mappers;

import utm.tn.dari.entities.Abonnement;
import utm.tn.dari.modules.abonnement.dtos.AbonnementCreateDto;
import utm.tn.dari.modules.abonnement.dtos.AbonnementDto;

public class AbonnementMapper {
    
    public static AbonnementDto toDto(Abonnement abonnement) {
        AbonnementDto dto = new AbonnementDto();
        dto.setId(abonnement.getId());
        dto.setNom(abonnement.getNom());
        dto.setDescription(abonnement.getDescription());
        dto.setPrix(abonnement.getPrix());
        dto.setType(abonnement.getType());
        return dto;
    }
    
    public static Abonnement toEntity(AbonnementCreateDto dto) {
        Abonnement abonnement = new Abonnement();
        abonnement.setNom(dto.getNom());
        abonnement.setDescription(dto.getDescription());
        abonnement.setPrix(dto.getPrix());
        abonnement.setType(dto.getType());
        return abonnement;
    }
}