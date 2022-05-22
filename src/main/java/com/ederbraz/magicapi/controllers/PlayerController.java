package com.ederbraz.magicapi.controllers;

import com.ederbraz.magicapi.dto.PlayerDTO;
import com.ederbraz.magicapi.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/players")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @GetMapping
    public ResponseEntity<Page<PlayerDTO>> findAll(Pageable pageable) {
        Page<PlayerDTO> page = playerService.findAll(pageable);
        return ResponseEntity.ok().body(page);
    }

    @GetMapping(value = "/{name}")
    public ResponseEntity<PlayerDTO> findByName(@PathVariable String name) {
        PlayerDTO dto = playerService.findByName(name);
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping
    public ResponseEntity<PlayerDTO> insert(@Valid @RequestBody PlayerDTO playerDTO) {
        PlayerDTO dto = playerService.insert(playerDTO);
        URI uri = UriComponentsBuilder.newInstance().path("/{id}").buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<PlayerDTO> update(@Valid @PathVariable Long id, @RequestBody PlayerDTO playerDTO) {
        PlayerDTO dto = playerService.update(id, playerDTO);
        return ResponseEntity.ok().body(dto);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        playerService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
