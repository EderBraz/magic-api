package com.ederbraz.magicapi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "player")
public class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "player_id")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "owner")
    @ToString.Exclude
    @JsonIgnore
    private List<Deck> deckList = new ArrayList<>();

    public Player(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}