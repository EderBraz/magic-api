package com.ederbraz.magicapi.factory;

import com.ederbraz.magicapi.dto.CardDTO;
import com.ederbraz.magicapi.dto.DeckDTO;
import com.ederbraz.magicapi.dto.PlayerDTO;
import com.ederbraz.magicapi.entities.Card;
import com.ederbraz.magicapi.entities.Deck;
import com.ederbraz.magicapi.entities.Player;

import java.util.ArrayList;

import static com.ederbraz.magicapi.enums.Language.PT_BR;

public class Factory {

    private Factory() {
    }

    public static Card createCard() {
        return new Card(1L, "Black Lotus", "7", PT_BR, true, 1000.0, 1);
    }

    public static CardDTO createCardDTO() {
        Card card = createCard();
        return new CardDTO(card);
    }

    public static Player createPlayer() {
        return new Player(1L, "Teste");
    }

    public static PlayerDTO createPlayerDTO() {
        Player player = createPlayer();
        return new PlayerDTO(player);
    }

    public static Deck createDeck() {
        Card card = createCard();
        ArrayList<Card> cardList = new ArrayList<>();
        cardList.add(card);
        Player player = createPlayer();
        return new Deck(1L, "Branco", player, cardList);
    }

    public static DeckDTO createDeckDTO() {
        Deck deck = createDeck();
        return new DeckDTO(deck);

    }
}
