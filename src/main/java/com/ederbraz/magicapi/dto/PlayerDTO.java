package com.ederbraz.magicapi.dto;

import com.ederbraz.magicapi.entities.Deck;
import com.ederbraz.magicapi.entities.Player;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class PlayerDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank(message = "Campo requerido")
    private String name;
    private List<DeckDTO> deckList = new ArrayList<>();

    public PlayerDTO(Player entity) {
        this.id = entity.getId();
        this.name = entity.getName();
    }

    public PlayerDTO(Player entity, List<Deck> deckList) {
        this(entity);
        deckList.forEach(deck -> this.deckList.add(new DeckDTO(deck)));
    }
}
