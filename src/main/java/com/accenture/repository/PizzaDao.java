package com.accenture.repository;

import com.accenture.model.Pizza;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PizzaDao extends JpaRepository<Pizza, UUID> {

    //Data JPA gèere toutes les méthodes de bases
    //mais dès qu'on veut faire une recherche perso -> ajout d'une méthode

    List<Pizza> findByNameContainingIgnoreCase(String name);
    List<Pizza> findByIngredientsNameContainingIgnoreCase(String ingredientName);

}
