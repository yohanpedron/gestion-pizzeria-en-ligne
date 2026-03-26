package com.accenture.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a pizza stored in the system.
 * A pizza has a name, a price per size, a list of ingredients,
 * and an "active" flag used for soft deletion.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Pizza {

    /** Unique identifier of the pizza. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Name of the pizza. */
    private String name;

    /**
     * Price of the pizza for each size.
     * Stored as a separate table using an element collection.
     */
    @ElementCollection
    @CollectionTable(name = "pizza_price", joinColumns = @JoinColumn(name = "pizza_id"))
    @MapKeyColumn(name = "size")
    @Column(name = "price")
    private Map<PizzaSize, Double> price;

    /**
     * List of ingredients used in the pizza.
     * Stored as a collection of embedded entities.
     */
    @ElementCollection
    @OneToMany(cascade = CascadeType.ALL)
    private List<Ingredient> ingredients;

    /**
     * Indicates whether the pizza is active.
     * Used for soft deletion instead of removing the record.
     */
    private boolean active;

    public Pizza(String name, Map<PizzaSize, Double> price, List<Ingredient> ingredients, boolean active) {
        this.name = name;
        this.price = price;
        this.ingredients = ingredients;
        this.active = active;
    }
}
