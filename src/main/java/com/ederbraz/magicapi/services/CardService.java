package com.ederbraz.magicapi.services;

import com.ederbraz.magicapi.dto.CardDTO;
import com.ederbraz.magicapi.entities.Card;
import com.ederbraz.magicapi.exceptions.DatabaseException;
import com.ederbraz.magicapi.repositories.CardRepository;
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
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    public Page<CardDTO> findAll(Pageable pageable) {
        Page<Card> page = cardRepository.findAll(pageable);
        return page.map(CardDTO::new);
    }

    public CardDTO findById(Long id) {
        Optional<Card> optional = cardRepository.findById(id);
        Card entity = optional.orElseThrow(() -> new EntityNotFoundException("Entity not found"));

        return new CardDTO(entity);
    }

    public CardDTO findByName(String name) {
        Optional<Card> optional = cardRepository.findByName(name);
        Card entity = optional.orElseThrow(() -> new EntityNotFoundException("Entity not found"));

        return new CardDTO(entity);
    }

    @Transactional
    public CardDTO insert(CardDTO dto) {
        Card entity = new Card();
        copyDtoToEntity(dto, entity);
        checkCardNameLanguage(entity.getLanguage().getLabel());

        cardRepository.save(entity);

        return new CardDTO(entity);
    }

    @Transactional
    public CardDTO update(Long id, CardDTO dto) {
        try {
            Card entity = cardRepository.getById(id);
            copyDtoToEntity(dto, entity);
            checkCardNameLanguage(entity.getLanguage().getLabel());

            cardRepository.save(entity);

            return new CardDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Id not found" + id);
        }
    }

    public void delete(Long id) {
        try {
            cardRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Id not found " + id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation");
        }
    }

    private void copyDtoToEntity(CardDTO dto, Card entity) {
        entity.setName(dto.getName());
        entity.setEdition(dto.getEdition());
        entity.setLanguage(dto.getLanguage());
        entity.setFoil(dto.isFoil());
        entity.setPrice(dto.getPrice());
        entity.setQuantity(dto.getQuantity());
    }

    private void checkCardNameLanguage(String language) {
        if (!language.equals("pt-br")) {
            throw new IllegalArgumentException("O nome da carta deve ser em portugues");
        }
    }

}
