package com.ederbraz.magicapi.services;

import com.ederbraz.magicapi.dto.DeckDTO;
import com.ederbraz.magicapi.dto.PlayerDTO;
import com.ederbraz.magicapi.entities.Deck;
import com.ederbraz.magicapi.entities.Player;
import com.ederbraz.magicapi.exceptions.DatabaseException;
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
public class PlayerService {

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    DeckRepository deckRepository;

    public Page<PlayerDTO> findAll(Pageable pageable) {
        Page<Player> page = playerRepository.findAll(pageable);
        return page.map(PlayerDTO::new);
    }

    public PlayerDTO findByName(String name) {
        Optional<Player> optional = playerRepository.findByName(name);
        Player player = optional.orElseThrow(() -> new EntityNotFoundException("Entity not found"));

        return new PlayerDTO(player, player.getDeckList());
    }

    @Transactional
    public PlayerDTO insert(PlayerDTO dto) {
        Player player = new Player();
        copyDtoToEntity(dto, player);
        playerRepository.save(player);

        return new PlayerDTO(player);
    }

    @Transactional
    public PlayerDTO update(Long id, PlayerDTO dto) {
        try {
            Player player = playerRepository.getById(id);
            copyDtoToEntity(dto, player);
            playerRepository.save(player);

            return new PlayerDTO(player);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Id not found " + id);
        }
    }

    public void delete(Long id) {
        try {
            playerRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Id not found " + id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Integrity violation");
        }
    }

    private void copyDtoToEntity(PlayerDTO dto, Player player) {
        player.setName(dto.getName());
        player.getDeckList().clear();
        for (DeckDTO deckDTO : dto.getDeckList()) {
            Deck deck = deckRepository.getById(deckDTO.getId());
            player.getDeckList().add(deck);
        }
    }
}
