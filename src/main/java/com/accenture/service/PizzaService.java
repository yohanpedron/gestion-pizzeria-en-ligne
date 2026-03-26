package com.accenture.service;

import com.accenture.exception.PizzaException;
import com.accenture.service.dto.PizzaPatchRequestDto;
import com.accenture.service.dto.PizzaRequestDto;
import com.accenture.service.dto.PizzaResponseDto;

import java.util.List;

/**
 * Business service for managing pizzas.
 * Provides operations for creation, update, deletion and search.
 */
public interface PizzaService {

    /**
     * Creates a new pizza.
     * @param dto data required to create the pizza
     * @return the created pizza as a DTO
     * @throws PizzaException if a business rule is violated
     * @throws IllegalArgumentException if the input data is invalid
     */
    PizzaResponseDto addPizza(PizzaRequestDto dto) throws PizzaException;

    /**
     * Soft-deletes a pizza by marking it inactive.
     * @param name the pizza name
     * @throws jakarta.persistence.EntityNotFoundException if the pizza does not exist
     */
    void deletePizza(String name);

    /**
     * Returns all pizzas.
     * @return list of pizzas
     */
    List<PizzaResponseDto> findAll();

    /**
     * Partially updates a pizza.
     * @param name the pizza name
     * @param request fields to update
     * @return the updated pizza
     * @throws jakarta.persistence.EntityNotFoundException if the pizza does not exist
     */
    PizzaResponseDto patchPizza(String name, PizzaPatchRequestDto request);

    /**
     * Finds pizzas whose name contains the given text.
     * @param name search text
     * @return matching pizzas
     */
    List<PizzaResponseDto> findByName(String name);

    /**
     * Finds pizzas containing a given ingredient.
     * @param ingredient ingredient name or fragment
     * @return matching pizzas
     */
    List<PizzaResponseDto> findByIngredient(String ingredient);
}
