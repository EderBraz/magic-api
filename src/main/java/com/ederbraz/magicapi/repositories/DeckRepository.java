package com.ederbraz.magicapi.repositories;

import com.ederbraz.magicapi.entities.Deck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeckRepository extends JpaRepository<Deck, Long> {
    Optional<Deck> findByName(String name);
}
