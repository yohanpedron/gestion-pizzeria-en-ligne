package com.accenture.service;

import com.accenture.mapper.PizzaMapper;
import com.accenture.model.Ingredient;
import com.accenture.model.Pizza;
import com.accenture.repository.PizzaDao;
import com.accenture.service.dto.PizzaPatchRequestDto;
import com.accenture.service.dto.PizzaRequestDto;
import com.accenture.service.dto.PizzaResponseDto;
import com.accenture.utils.Messages;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class PizzaServiceImpl implements PizzaService {

    private final PizzaDao pizzaDao;
    private final PizzaMapper pizzaMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public PizzaResponseDto addPizza(PizzaRequestDto request) {

        if (request.name() == null || request.name().isBlank()) {
            throw new IllegalArgumentException(Messages.PIZZA_NAME_EMPTY);}
        if (request.price() == null || request.price().isEmpty()) {
            throw new IllegalArgumentException(Messages.PIZZA_PRICE_EMPTY);}
        if (request.ingredients() == null || request.ingredients().isEmpty()) {
            throw new IllegalArgumentException(Messages.PIZZA_INGREDIENTS_EMPTY);}
        if (pizzaDao.findByNameIgnoreCase(request.name()).isPresent()) {
            throw new EntityExistsException(Messages.PIZZA_ALREADY_EXISTS);}
        if (request.price().values().stream().anyMatch(p -> p <= 0)) {
            throw new IllegalArgumentException(Messages.PIZZA_PRICE_NEGATIVE);}

        Pizza pizza = pizzaMapper.toPizza(request);
        Pizza saved = pizzaDao.save(pizza);
        return pizzaMapper.toPizzaResponseDto(saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deletePizza(String name) {
        Pizza pizza = pizzaDao.findByNameIgnoreCase(name)
                .orElseThrow(EntityNotFoundException::new);

        pizza.setActive(false);

        pizzaDao.save(pizza);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PizzaResponseDto> findAll() {
        return pizzaDao.findAll()
                .stream()
                .map(pizzaMapper::toPizzaResponseDto)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PizzaResponseDto patchPizza(String name, PizzaPatchRequestDto request) {

        Pizza pizza = pizzaDao.findByNameIgnoreCase(name)
                .orElseThrow(() -> new EntityNotFoundException(Messages.PIZZA_NOT_FOUND));

        if (request.name() != null && !request.name().isBlank()) {
            pizza.setName(request.name());
        }

        if (request.price() != null && !request.price().isEmpty()) {
            pizza.setPrice(request.price());
        }

        if (request.ingredients() != null && !request.ingredients().isEmpty()) {
            List<Ingredient> ingredients = request.ingredients().stream()
                    .map(dto -> new Ingredient(dto.name(), 10))
                    .toList();
            pizza.setIngredients(ingredients);
        }

        Pizza saved = pizzaDao.save(pizza);
        return pizzaMapper.toPizzaResponseDto(saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PizzaResponseDto> findByName(String name) {
        return pizzaDao.findByNameContainingIgnoreCase(name)
                .stream()
                .map(pizzaMapper::toPizzaResponseDto)
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PizzaResponseDto> findByIngredient(String ingredient) {
        return pizzaDao.findByIngredientsNameContainingIgnoreCase(ingredient)
                .stream()
                .map(pizzaMapper::toPizzaResponseDto)
                .toList();
    }

}
