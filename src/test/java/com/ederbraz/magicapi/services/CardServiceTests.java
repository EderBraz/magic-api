package com.ederbraz.magicapi.services;

import com.ederbraz.magicapi.dto.CardDTO;
import com.ederbraz.magicapi.entities.Card;
import com.ederbraz.magicapi.exceptions.DatabaseException;
import com.ederbraz.magicapi.factory.Factory;
import com.ederbraz.magicapi.repositories.CardRepository;
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
class CardServiceTests {

    @InjectMocks
    private CardService cardService;

    @Mock
    private CardRepository cardRepository;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private String existingName;
    private String nonExistingName;
    private CardDTO dto;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        existingName = "Black Lotus";
        nonExistingName = "Error";
        Card card = Factory.createCard();
        dto = Factory.createCardDTO();
        PageImpl<Card> page = new PageImpl<>(List.of(card));

        Mockito.when(cardRepository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);

        Mockito.when(cardRepository.findById(existingId)).thenReturn(Optional.of(card));
        Mockito.when(cardRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        Mockito.when(cardRepository.findByName(existingName)).thenReturn(Optional.of(card));
        Mockito.when(cardRepository.findByName(nonExistingName)).thenReturn(Optional.empty());

        Mockito.when(cardRepository.save(ArgumentMatchers.any())).thenReturn(card);

        Mockito.when(cardRepository.getById(existingId)).thenReturn(card);
        Mockito.doThrow(EntityNotFoundException.class).when(cardRepository).getById(nonExistingId);

        Mockito.doNothing().when(cardRepository).deleteById(existingId);
        Mockito.doThrow(EmptyResultDataAccessException.class).when(cardRepository).deleteById(nonExistingId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(cardRepository).deleteById(dependentId);
    }

    @Test
    void findAllPagedShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<CardDTO> result = cardService.findAll(pageable);

        Assertions.assertNotNull(result);

        Mockito.verify(cardRepository).findAll(pageable);
    }

    @Test
    void findByIdShouldReturnCardDTOWhenIdExists() {

        CardDTO card = cardService.findById(existingId);
        Assertions.assertNotNull(card);

        Mockito.verify(cardRepository).findById(existingId);
    }

    @Test
    void findByIdShouldThrowEntityNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> cardService.findById(nonExistingId));

        Mockito.verify(cardRepository).findById(nonExistingId);
    }

    @Test
    void findByNameShouldReturnCardDTOWhenNameExists() {
        CardDTO card = cardService.findByName(existingName);
        Assertions.assertNotNull(card);

        Mockito.verify(cardRepository).findByName(existingName);
    }

    @Test
    void findByNameShouldThrowEntityNotFoundExceptionWhenNameDoesNotExist() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> cardService.findByName(nonExistingName));

        Mockito.verify(cardRepository).findByName(nonExistingName);
    }

    @Test
    void updateShouldReturnCardDTOWhenIdExists() {
        CardDTO card = cardService.update(existingId, dto);
        Assertions.assertNotNull(dto);
        Mockito.verify(cardRepository).getById(existingId);
    }

    @Test
    void updateShouldThrowEntityNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> cardService.update(nonExistingId, dto));

        Mockito.verify(cardRepository).getById(nonExistingId);
    }

    @Test
    void deleteShouldDoNothingWhenIdExists() {
        Assertions.assertDoesNotThrow(() -> cardService.delete(existingId));

        Mockito.verify(cardRepository).deleteById(existingId);
    }

    @Test
    void deleteShouldThrowEntityNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> cardService.delete(nonExistingId));

        Mockito.verify(cardRepository).deleteById(nonExistingId);
    }

    @Test
    void deleteShouldThrowDatabaseExceptionWhenDependentId() {
        Assertions.assertThrows(DatabaseException.class, () -> cardService.delete(dependentId));

        Mockito.verify(cardRepository).deleteById(dependentId);
    }
}
