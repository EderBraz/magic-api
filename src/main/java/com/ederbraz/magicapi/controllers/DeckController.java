package com.ederbraz.magicapi.controllers;

import com.ederbraz.magicapi.dto.DeckDTO;
import com.ederbraz.magicapi.services.DeckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/decks")
public class DeckController {

    @Autowired
    private DeckService deckService;

    @GetMapping
    public ResponseEntity<Page<DeckDTO>> findAll(Pageable pageable) {
        Page<DeckDTO> page = deckService.findAll(pageable);
        return ResponseEntity.ok().body(page);
    }

    @GetMapping(value = "/{name}")
    public ResponseEntity<DeckDTO> findById(@PathVariable String name) {
        DeckDTO dto = deckService.findByName(name);
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping
    public ResponseEntity<DeckDTO> insert(@Valid @RequestBody DeckDTO deckDTO) {
        DeckDTO dto = deckService.insert(deckDTO);
        URI uri = URI.create(String.format("/decks/%s", dto.getId()));
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping(value = "/player/{playerName}/deck/{id}")
    public ResponseEntity<DeckDTO> update(@Valid @PathVariable String playerName, @PathVariable Long id, @RequestBody DeckDTO deckDTO) {
        DeckDTO dto = deckService.update(id, deckDTO, playerName);
        return ResponseEntity.ok().body(dto);
    }

    @DeleteMapping(value = "/player/{playerName}/deck/{id}")
    public ResponseEntity<Void> delete(@PathVariable String playerName, @PathVariable Long id) {
        deckService.delete(id, playerName);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/player/{playerName}/deck/{idDeck}/addCard/{idCard}")
    public ResponseEntity<DeckDTO> addCard(@PathVariable String playerName, @PathVariable Long idDeck, @PathVariable Long idCard) {
        DeckDTO dto = deckService.addCard(idDeck, idCard, playerName);
        return ResponseEntity.ok().body(dto);
    }

    @DeleteMapping("/player/{playerName}/deck/{idDeck}/removeCard/{idCard}")
    public ResponseEntity<Void> removeCard(@PathVariable String playerName, @PathVariable Long idDeck, @PathVariable Long idCard) {
        deckService.removeCard(idDeck, idCard, playerName);
        return ResponseEntity.noContent().build();
    }
}
