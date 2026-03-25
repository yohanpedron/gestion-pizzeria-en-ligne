package com.accenture.service;

import com.accenture.exception.PizzaException;
import com.accenture.mapper.PizzaMapper;
import com.accenture.model.Pizza;
import com.accenture.repository.PizzaDao;
import com.accenture.service.dto.PizzaRequestDto;
import com.accenture.service.dto.PizzaResponseDto;
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
    public PizzaResponseDto addPizza(PizzaRequestDto dto) throws PizzaException {
        Pizza pizza = pizzaMapper.toPizza(dto);
        Pizza savedPizza = pizzaDao.save(pizza);
        return pizzaMapper.toPizzaResponseDto(savedPizza);
    }

    @Override
    public void deletePizza(UUID id) throws PizzaException {
        Pizza pizza = pizzaDao.getReferenceById(id);
        pizzaDao.delete(pizza);
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
    public PizzaResponseDto patchPizza(UUID districtId, PizzaRequestDto requestDto) {
        return null;
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
