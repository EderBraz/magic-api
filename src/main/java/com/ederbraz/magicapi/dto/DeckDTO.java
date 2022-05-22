package com.ederbraz.magicapi.dto;

import com.ederbraz.magicapi.entities.Deck;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class DeckDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank
    private String name;

    private String owner;

    private List<CardDTO> cardList = new ArrayList<>();

    public DeckDTO(Deck entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.owner = entity.getOwner().getName();
        entity.getCardList().forEach(card -> this.getCardList().add(new CardDTO(card)));
    }
}
