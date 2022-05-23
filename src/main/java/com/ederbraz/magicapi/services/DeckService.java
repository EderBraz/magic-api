package com.ederbraz.magicapi.services;

import com.ederbraz.magicapi.dto.CardDTO;
import com.ederbraz.magicapi.dto.DeckDTO;
import com.ederbraz.magicapi.entities.Card;
import com.ederbraz.magicapi.entities.Deck;
import com.ederbraz.magicapi.entities.Player;
import com.ederbraz.magicapi.exceptions.DatabaseException;
import com.ederbraz.magicapi.repositories.CardRepository;
import com.ederbraz.magicapi.repositories.DeckRepository;
import com.ederbraz.magicapi.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class DeckService {

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private PlayerRepository playerRepository;

    public Page<DeckDTO> findAll(Pageable pageable) {
        Page<Deck> page = deckRepository.findAll(pageable);
        return page.map(DeckDTO::new);
    }


    public DeckDTO findByName(String name) {
        Optional<Deck> optional = deckRepository.findByName(name);
        Deck entity = optional.orElseThrow(() -> new EntityNotFoundException("Entity not found"));

        return new DeckDTO(entity);
    }

    @Transactional
    public DeckDTO insert(DeckDTO dto) {
        Deck deck = new Deck();
        copyDtoToEntity(dto, deck);
        deckRepository.save(deck);

        return new DeckDTO(deck);
    }

    @Transactional
    public DeckDTO update(Long id, DeckDTO dto, String playerName) {
        try {
            Deck deck = deckRepository.getById(id);
            checkIfPlayerIsOwner(playerName, deck.getOwner().getName());
            copyDtoToEntity(dto, deck);
            deckRepository.save(deck);

            return new DeckDTO(deck);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Id not found " + id);
        }
    }

    public void delete(Long id, String playerName) {
        try {
            Deck deck = deckRepository.getById(id);
            checkIfPlayerIsOwner(playerName, deck.getOwner().getName());
            deckRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Id not found " + id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation");
        }
    }

    public DeckDTO addCard(Long idDeck, Long idCard, String playerName) {
        try {
            Deck deck = deckRepository.getById(idDeck);
            checkIfPlayerIsOwner(playerName, deck.getOwner().getName());
            Card card = cardRepository.getById(idCard);
            deck.getCardList().add(card);
            return new DeckDTO(deck);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Id not found");
        }
    }

    public void removeCard(Long idDeck, Long idCard, String playerName) {
        try {
            Deck deck = deckRepository.getById(idDeck);
            checkIfPlayerIsOwner(playerName, deck.getOwner().getName());
            Card card = cardRepository.getById(idCard);
            deck.getCardList().remove(card);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Id not found");
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation");
        }
    }

    private void copyDtoToEntity(DeckDTO dto, Deck entity) {
        entity.setName(dto.getName());
        Player player = playerRepository.getByName(dto.getOwner());
        entity.setOwner(player);
        entity.getCardList().clear();
        for (CardDTO cardDTO : dto.getCardList()) {
            Card card = cardRepository.getById(cardDTO.getId());
            entity.getCardList().add(card);
        }
    }
    private void checkIfPlayerIsOwner(String player, String owner) {
        if(!player.equals(owner)){
            throw new IllegalArgumentException("You cannot change a deck from another player");
        }
    }
}
