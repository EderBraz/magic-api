package com.ederbraz.magicapi.services;

import com.ederbraz.magicapi.dto.PlayerDTO;
import com.ederbraz.magicapi.entities.Player;
import com.ederbraz.magicapi.exceptions.DatabaseException;
import com.ederbraz.magicapi.factory.Factory;
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
class PlayerServiceTests {

    @InjectMocks
    private PlayerService playerService;

    @Mock
    private PlayerRepository playerRepository;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private String existingName;
    private String nonExistingName;
    private PlayerDTO dto;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        existingName = "Black Lotus";
        nonExistingName = "Error";
        Player player = Factory.createPlayer();
        dto = Factory.createPlayerDTO();
        PageImpl<Player> page = new PageImpl<>(List.of(player));

        Mockito.when(playerRepository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);

        Mockito.when(playerRepository.findById(existingId)).thenReturn(Optional.of(player));
        Mockito.when(playerRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        Mockito.when(playerRepository.findByName(existingName)).thenReturn(Optional.of(player));
        Mockito.when(playerRepository.findByName(nonExistingName)).thenReturn(Optional.empty());

        Mockito.when(playerRepository.save(ArgumentMatchers.any())).thenReturn(player);

        Mockito.when(playerRepository.getById(existingId)).thenReturn(player);
        Mockito.doThrow(EntityNotFoundException.class).when(playerRepository).getById(nonExistingId);

        Mockito.doNothing().when(playerRepository).deleteById(existingId);
        Mockito.doThrow(EmptyResultDataAccessException.class).when(playerRepository).deleteById(nonExistingId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(playerRepository).deleteById(dependentId);
    }

    @Test
    void findAllPagedShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<PlayerDTO> result = playerService.findAll(pageable);

        Assertions.assertNotNull(result);

        Mockito.verify(playerRepository).findAll(pageable);
    }

    @Test
    void findByNameShouldReturnCardDTOWhenNameExists() {
        PlayerDTO dto = playerService.findByName(existingName);
        Assertions.assertNotNull(dto);

        Mockito.verify(playerRepository).findByName(existingName);
    }

    @Test
    void findByNameShouldThrowEntityNotFoundExceptionWhenNameDoesNotExist() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> playerService.findByName(nonExistingName));

        Mockito.verify(playerRepository).findByName(nonExistingName);
    }

    @Test
    void updateShouldReturnCardDTOWhenIdExists() {
        PlayerDTO playerDTO = playerService.update(existingId, dto);
        Assertions.assertNotNull(playerDTO);
        Mockito.verify(playerRepository).getById(existingId);
    }

    @Test
    void updateShouldThrowEntityNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> playerService.update(nonExistingId, dto));

        Mockito.verify(playerRepository).getById(nonExistingId);
    }

    @Test
    void deleteShouldDoNothingWhenIdExists() {
        Assertions.assertDoesNotThrow(() -> playerService.delete(existingId));

        Mockito.verify(playerRepository).deleteById(existingId);
    }

    @Test
    void deleteShouldThrowEntityNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> playerService.delete(nonExistingId));

        Mockito.verify(playerRepository).deleteById(nonExistingId);
    }

    @Test
    void deleteShouldThrowDatabaseExceptionWhenDependentId() {
        Assertions.assertThrows(DatabaseException.class, () -> playerService.delete(dependentId));

        Mockito.verify(playerRepository).deleteById(dependentId);
    }
}
