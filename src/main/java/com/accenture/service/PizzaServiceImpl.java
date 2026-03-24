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

    }

    @Override
    public List<PizzaResponseDto> findAll() {
        return List.of();
    }

    @Override
    public PizzaResponseDto findById(UUID id) {
        return null;
    }

    @Override
    public PizzaResponseDto putPizza(UUID districtId, PizzaRequestDto requestDto) {
        return null;
    }

    @Override
    public PizzaResponseDto patchPizza(UUID districtId, PizzaRequestDto requestDto) {
        return null;
    }
}
