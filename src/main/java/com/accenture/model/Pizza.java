package com.accenture.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Pizza {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;

    @ElementCollection
    @CollectionTable(name = "pizza_price", joinColumns = @JoinColumn(name = "pizza_id"))
    @MapKeyColumn(name = "size")
    @Column(name = "price")
    private Map<PizzaSize, Double> price;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Ingredient> ingredients;
    private boolean active;
}
