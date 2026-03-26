package com.accenture.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Represents a single line of an order.
 * Each row contains one pizza, its size and the number of extra ingredients.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRowDescription {

    /** Unique identifier of the row. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Pizza included in this row. */
    @OneToOne
    @JoinColumn
    private Pizza pizza;

    /** Size of the pizza. */
    private PizzaSize size;

    /** Number of extra ingredients added to the pizza. */
    private int ingredientAmount;

    public OrderRowDescription(Pizza pizza, PizzaSize size, int ingredientAmount) {
        this.pizza = pizza;
        this.size = size;
        this.ingredientAmount = ingredientAmount;
    }
}
