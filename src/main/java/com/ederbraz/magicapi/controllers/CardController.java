package com.ederbraz.magicapi.controllers;

import com.ederbraz.magicapi.dto.CardDTO;
import com.ederbraz.magicapi.services.CardService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/cards")
public class CardController {

    @Autowired
    private CardService cardService;

    @GetMapping
    @ApiOperation("Show all cards paged")
    public ResponseEntity<Page<CardDTO>> findAll(Pageable pageable) {
        Page<CardDTO> page = cardService.findAll(pageable);
        return ResponseEntity.ok().body(page);
    }

    @GetMapping(value = "/{id}")
    @ApiOperation("Find card by id")
    public ResponseEntity<CardDTO> findById(@PathVariable Long id) {
        CardDTO dto = cardService.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    @GetMapping(value = "/{name}")
    @ApiOperation("Find card by name")
    public ResponseEntity<CardDTO> findByName(@PathVariable String name) {
        CardDTO dto = cardService.findByName(name);
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping
    @ApiOperation("Create new card")
    public ResponseEntity<CardDTO> insert(@Valid @RequestBody CardDTO cardDTO) {
        CardDTO dto = cardService.insert(cardDTO);
        URI uri = URI.create(String.format("/cards/%s", dto.getId()));
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping(value = "/{id}")
    @ApiOperation("Update card")
    public ResponseEntity<CardDTO> update(@Valid @PathVariable Long id, @RequestBody CardDTO cardDTO) {
        CardDTO dto = cardService.update(id, cardDTO);
        return ResponseEntity.ok().body(dto);
    }

    @DeleteMapping(value = "/{id}")
    @ApiOperation("Delete card")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cardService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
