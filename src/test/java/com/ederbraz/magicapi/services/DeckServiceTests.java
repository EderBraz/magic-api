package com.ederbraz.magicapi.services;

import com.ederbraz.magicapi.dto.DeckDTO;
import com.ederbraz.magicapi.entities.Card;
import com.ederbraz.magicapi.entities.Deck;
import com.ederbraz.magicapi.entities.Player;
import com.ederbraz.magicapi.exceptions.DatabaseException;
import com.ederbraz.magicapi.factory.Factory;
import com.ederbraz.magicapi.repositories.CardRepository;
import com.ederbraz.magicapi.repositories.DeckRepository;
import com.ederbraz.magicapi.repositories.PlayerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
class DeckServiceTests {

    @InjectMocks
    private DeckService deckService;

    @Mock
    private DeckRepository deckRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private CardRepository cardRepository;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private String existingDeckName;
    private String nonExistingDeckName;
    private String ownerName;
    private String nonOwnerName;
    private DeckDTO dto;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 2L;
        existingDeckName = "Branco";
        nonExistingDeckName = "Error";
        ownerName = "Teste";
        nonOwnerName = "Invalid";
        Deck deck = Factory.createDeck();
        dto = Factory.createDeckDTO();
        Player player = Factory.createPlayer();
        Card card = Factory.createCard();
        PageImpl<Deck> page = new PageImpl<>(List.of(deck));

        Mockito.when(deckRepository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);

        Mockito.when(deckRepository.findById(existingId)).thenReturn(Optional.of(deck));
        Mockito.when(deckRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        Mockito.when(deckRepository.findByName(existingDeckName)).thenReturn(Optional.of(deck));
        Mockito.when(deckRepository.findByName(nonExistingDeckName)).thenReturn(Optional.empty());

        Mockito.when(deckRepository.save(ArgumentMatchers.any())).thenReturn(deck);

        Mockito.when(deckRepository.getById(existingId)).thenReturn(deck);
        Mockito.doThrow(EntityNotFoundException.class).when(deckRepository).getById(nonExistingId);

        Mockito.doNothing().when(deckRepository).deleteById(existingId);
        Mockito.doThrow(EmptyResultDataAccessException.class).when(deckRepository).deleteById(nonExistingId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(deckRepository).deleteById(dependentId);

        Mockito.when(playerRepository.getByName(ownerName)).thenReturn(player);
        Mockito.doThrow(EntityNotFoundException.class).when(playerRepository).getByName(nonOwnerName);

        Mockito.when(cardRepository.getById(existingId)).thenReturn(card);
        Mockito.doThrow(EntityNotFoundException.class).when(cardRepository).getById(nonExistingId);
    }

    @Test
    void findAllPagedShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<DeckDTO> result = deckService.findAll(pageable);

        Assertions.assertNotNull(result);

        Mockito.verify(deckRepository).findAll(pageable);
    }

    @Test
    void findByNameShouldReturnCardDTOWhenNameExists() {
        DeckDTO dto = deckService.findByName(existingDeckName);
        Assertions.assertNotNull(dto);

        Mockito.verify(deckRepository).findByName(existingDeckName);
    }

    @Test
    void findByNameShouldThrowEntityNotFoundExceptionWhenNameDoesNotExist() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> deckService.findByName(nonExistingDeckName));

        Mockito.verify(deckRepository).findByName(nonExistingDeckName);
    }

    @Test
    void updateShouldReturnCardDTOWhenIdExists() {
        DeckDTO deckDTO = deckService.update(existingId, dto, ownerName);
        Assertions.assertNotNull(deckDTO);
        Mockito.verify(deckRepository).getById(existingId);
    }

    @Test
    void updateShouldThrowEntityNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> deckService.update(nonExistingId, dto, ownerName));

        Mockito.verify(deckRepository).getById(nonExistingId);
    }

    @Test
    void updateShouldThrowIllegalArgumentExceptionWhenUserDifOwner() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> deckService.update(existingId, dto, nonOwnerName));
    }

    @Test
    void deleteShouldDoNothingWhenIdExists() {
        Assertions.assertDoesNotThrow(() -> deckService.delete(existingId, ownerName));

        Mockito.verify(deckRepository).getById(existingId);
        Mockito.verify(deckRepository).deleteById(existingId);
    }

    @Test
    void deleteShouldThrowEntityNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> deckService.delete(nonExistingId, ownerName));

        Mockito.verify(deckRepository).getById(nonExistingId);
    }

    @Test
    void deleteShouldThrowDatabaseExceptionWhenDependentId() {
        Assertions.assertThrows(DatabaseException.class, () -> deckService.delete(dependentId, ownerName));

        Mockito.verify(deckRepository).deleteById(dependentId);
    }

    @Test
    void deleteShouldIllegalArgumentExceptionWhenUserDifOwner() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> deckService.delete(existingId, nonOwnerName));

        Mockito.verify(deckRepository).getById(existingId);

    }
}
