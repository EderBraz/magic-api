package com.ederbraz.magicapi.repositories;

import com.ederbraz.magicapi.entities.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByName(String name);

    Player getByName(String name);
}
