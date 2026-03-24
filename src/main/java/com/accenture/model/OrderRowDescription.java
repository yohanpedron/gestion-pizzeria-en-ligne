package com.accenture.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRowDescription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn
    private Pizza pizza;

    private PizzaSize size;
    private int ingredientAmount;

    public OrderRowDescription(Pizza pizza, PizzaSize size, int ingredientAmount) {
        this.pizza = pizza;
        this.size = size;
        this.ingredientAmount = ingredientAmount;
    }
}
