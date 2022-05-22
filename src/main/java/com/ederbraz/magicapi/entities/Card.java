package com.ederbraz.magicapi.entities;

import com.ederbraz.magicapi.enums.Language;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "card")
public class Card implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Long id;

    private String name;
    private String edition;
    private Language language;
    private boolean foil;
    private double price;
    private int quantity;

    @ManyToMany(mappedBy = "cardList")
    @ToString.Exclude
    @JsonIgnore
    private List<Deck> deckList = new ArrayList<>();

}
