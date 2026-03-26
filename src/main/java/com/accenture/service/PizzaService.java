package com.accenture.service;

import com.accenture.exception.PizzaException;
import com.accenture.service.dto.PizzaPatchRequestDto;
import com.accenture.service.dto.PizzaRequestDto;
import com.accenture.service.dto.PizzaResponseDto;

import java.util.List;

public interface PizzaService {

    PizzaResponseDto addPizza(PizzaRequestDto dto) throws PizzaException;

    void deletePizza(String name);

    List<PizzaResponseDto> findAll();

    PizzaResponseDto patchPizza(String name, PizzaPatchRequestDto request);

    List<PizzaResponseDto> findByName(String name);

    List<PizzaResponseDto> findByIngredient(String ingredient);
}
