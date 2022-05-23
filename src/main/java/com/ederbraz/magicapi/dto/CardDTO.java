package com.ederbraz.magicapi.dto;

import com.ederbraz.magicapi.entities.Card;
import com.ederbraz.magicapi.enums.Language;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class CardDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;

    @Size(min = 3, max = 50, message = "Deve ter entre 3 e 50 caracteres")
    @NotBlank(message = "Campo requerido")
    private String name;

    @NotBlank(message = "Campo requerido")
    private String edition;

    @NotNull(message = "Campo requerido")
    private Language language;

    @NotNull(message = "Campo requerido")
    private boolean foil;

    @Positive(message = "Preço deve ser um valor positivo")
    private double price;

    @Min(value = 1, message = "quantidade minima de uma carta.")
    private int quantity;

    public CardDTO(Card entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.edition = entity.getEdition();
        this.language = entity.getLanguage();
        this.foil = entity.isFoil();
        this.price = entity.getPrice();
        this.quantity = entity.getQuantity();
    }

}
