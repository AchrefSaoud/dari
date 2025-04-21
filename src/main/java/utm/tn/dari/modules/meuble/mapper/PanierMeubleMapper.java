package utm.tn.dari.modules.meuble.mapper;

import utm.tn.dari.entities.Meuble;
import utm.tn.dari.entities.PanierMeuble;
import utm.tn.dari.entities.PanierMeubleItem;
import utm.tn.dari.entities.User;
import utm.tn.dari.modules.meuble.dto.PanierDTO;
import utm.tn.dari.modules.meuble.dto.PanierItemDTO;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PanierMeubleMapper {

    public static PanierDTO toDto(PanierMeuble panier) {
        if (panier == null) {
            return null;
        }

        PanierDTO dto = new PanierDTO();
        dto.setAcheteurId(panier.getAcheteur() != null ? panier.getAcheteur().getId() : null);
        dto.setTotal(panier.getTotal());

        List<PanierMeubleItem> items = panier.getItems();
        if (items != null && !items.isEmpty()) {
            List<PanierItemDTO> itemDTOs = items.stream()
                .filter(Objects::nonNull)
                .map(PanierMeubleMapper::toItemDto)
                .collect(Collectors.toList());
            dto.setItems(itemDTOs);
        } else {
            dto.setItems(Collections.emptyList());
        }

        return dto;
    }

    private static PanierItemDTO toItemDto(PanierMeubleItem item) {
        PanierItemDTO itemDTO = new PanierItemDTO();
        itemDTO.setMeubleId(item.getMeuble() != null ? item.getMeuble().getId() : null);
        itemDTO.setQuantite(item.getQuantite());
        itemDTO.setSousTotal(item.getSousTotal());
        return itemDTO;
    }

    public static PanierMeuble toEntity(PanierDTO dto, User acheteur) {
        if (dto == null) {
            return null;
        }

        PanierMeuble panier = new PanierMeuble();
        panier.setAcheteur(acheteur);
        panier.setTotal(dto.getTotal());

        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            List<PanierMeubleItem> items = dto.getItems().stream()
                .filter(Objects::nonNull)
                .map(itemDto -> toItemEntity(itemDto, panier))
                .collect(Collectors.toList());
            panier.setItems(items);
        }

        return panier;
    }

    private static PanierMeubleItem toItemEntity(PanierItemDTO dto, PanierMeuble panier) {
        PanierMeubleItem item = new PanierMeubleItem();
        
        Meuble meuble = new Meuble();
        meuble.setId(dto.getMeubleId());
        item.setMeuble(meuble);
        
        item.setQuantite(dto.getQuantite());
        item.setSousTotal(dto.getSousTotal());
        item.setPanier(panier);
        
        return item;
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