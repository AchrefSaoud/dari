package utm.tn.dari.modules.meuble.mapper;

import utm.tn.dari.entities.Meuble;
import utm.tn.dari.entities.PanierMeuble;
import utm.tn.dari.entities.PanierMeubleItem;
import utm.tn.dari.entities.User;
import utm.tn.dari.modules.meuble.dto.PanierDTO;
import utm.tn.dari.modules.meuble.dto.PanierItemDTO;

import java.util.List;
import java.util.stream.Collectors;

public class PanierMeubleMapper {

    public static PanierDTO toDto(PanierMeuble panier) {
        PanierDTO dto = new PanierDTO();
        dto.setId(panier.getId());
        dto.setAcheteurId(panier.getAcheteur().getId());
        dto.setTotal(panier.getTotal());

        if (panier.getItems() != null) {
            List<PanierItemDTO> itemDTOs = panier.getItems().stream().map(item -> {
                PanierItemDTO itemDTO = new PanierItemDTO();
                itemDTO.setMeubleId(item.getMeuble().getId());
                itemDTO.setQuantite(item.getQuantite());
                itemDTO.setSousTotal(item.getSousTotal());
                return itemDTO;
            }).collect(Collectors.toList());
            dto.setItems(itemDTOs);
        }

        return dto;
    }

    public static PanierMeuble toEntity(PanierDTO dto, User acheteur) {
        PanierMeuble panier = new PanierMeuble();
        panier.setAcheteur(acheteur);
        panier.setTotal(dto.getTotal());
        return panier;
    }

    public static List<PanierMeubleItem> mapItems(List<PanierItemDTO> items, PanierMeuble panier) {
        return items.stream().map(dto -> {
            PanierMeubleItem item = new PanierMeubleItem();
            Meuble meuble = new Meuble();
            meuble.setId(dto.getMeubleId());
            item.setMeuble(meuble);
            item.setQuantite(dto.getQuantite());
            item.setSousTotal(dto.getSousTotal());
            item.setPanier(panier);
            return item;
        }).collect(Collectors.toList());
    }
}