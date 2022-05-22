package com.ederbraz.magicapi.factory;

import com.ederbraz.magicapi.dto.CardDTO;
import com.ederbraz.magicapi.entities.Card;

import java.util.ArrayList;

import static com.ederbraz.magicapi.enums.Language.PT_BR;

public class Factory {

    public static Card createCard() {
        return new Card(1L, "Black Lotus", "7", PT_BR, true, 1000.0,  1, new ArrayList<>());
    }

    public static CardDTO createCardDTO() {
        Card card = createCard();
        return new CardDTO(card);
    }
}
