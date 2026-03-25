package com.accenture.service;

import com.accenture.mapper.PizzaMapper;
import com.accenture.model.Ingredient;
import com.accenture.model.Pizza;
import com.accenture.repository.PizzaDao;
import com.accenture.service.dto.PizzaPatchRequestDto;
import com.accenture.service.dto.PizzaRequestDto;
import com.accenture.service.dto.PizzaResponseDto;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class PizzaServiceImpl implements PizzaService {

    private final PizzaDao pizzaDao;
    private final PizzaMapper pizzaMapper;
    private final MessageSourceAccessor messages;

    @Override
    public PizzaResponseDto addPizza(PizzaRequestDto request) {

        if (request.name() == null || request.name().isBlank()) {
            throw new IllegalArgumentException("Pizza name cannot be empty");}
        if (request.price() == null || request.price().isEmpty()) {
            throw new IllegalArgumentException("Pizza price cannot be empty");}
        if (request.ingredients() == null || request.ingredients().isEmpty()) {
            throw new IllegalArgumentException("Pizza must contain at least one ingredient");}
        if (pizzaDao.findByNameIgnoreCase(request.name()).isPresent()) {
            throw new EntityExistsException("Pizza with this name already exists");}

        Pizza pizza = pizzaMapper.toPizza(request);
        Pizza saved = pizzaDao.save(pizza);
        return pizzaMapper.toPizzaResponseDto(saved);
    }

    @Override
    public void deletePizza(String name) {
        Pizza pizza = pizzaDao.findByNameIgnoreCase(name)
                .orElseThrow(EntityNotFoundException::new);

        pizza.setActive(false);

        pizzaDao.save(pizza);
    }

    @Override
    public List<PizzaResponseDto> findAll() {
        return pizzaDao.findAll()
                .stream()
                .map(pizzaMapper::toPizzaResponseDto)
                .toList();
    }

    @Override
    public PizzaResponseDto findById(UUID id) {
        Pizza pizza = pizzaDao.getReferenceById(id);
        return pizzaMapper.toPizzaResponseDto(pizza);
    }

    @Override
    public PizzaResponseDto putPizza(UUID districtId, PizzaRequestDto requestDto) {
        return null;
    }

    @Override
    public PizzaResponseDto patchPizza(String name, PizzaPatchRequestDto request) {

        Pizza pizza = pizzaDao.findByNameIgnoreCase(name)
                .orElseThrow(() -> new EntityNotFoundException("Pizza not found"));

        // PATCH : mise à jour uniquement des champs présents
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

//        if (request.active() != null) {
//            pizza.setActive(request.active());
//        }

        Pizza saved = pizzaDao.save(pizza);
        return pizzaMapper.toPizzaResponseDto(saved);
    }


    @Override
    public List<PizzaResponseDto> findByName(String name) {
        return pizzaDao.findByNameContainingIgnoreCase(name)
                .stream()
                .map(pizzaMapper::toPizzaResponseDto)
                .toList();
    }

    @Override
    public List<PizzaResponseDto> findByIngredient(String ingredient) {
        return pizzaDao.findByIngredientsNameContainingIgnoreCase(ingredient)
                .stream()
                .map(pizzaMapper::toPizzaResponseDto)
                .toList();
    }

}
