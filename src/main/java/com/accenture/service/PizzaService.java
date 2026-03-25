package com.accenture.service;

import com.accenture.exception.PizzaException;
import com.accenture.service.dto.PizzaPatchRequestDto;
import com.accenture.service.dto.PizzaRequestDto;
import com.accenture.service.dto.PizzaResponseDto;

import java.util.List;
import java.util.UUID;

public interface PizzaService {

    PizzaResponseDto addPizza(PizzaRequestDto dto) throws PizzaException;

    void deletePizza(String name);

    List<PizzaResponseDto> findAll();
    PizzaResponseDto findById(UUID id);

    PizzaResponseDto putPizza(UUID districtId, PizzaRequestDto requestDto);

    PizzaResponseDto patchPizza(String name, PizzaPatchRequestDto request);

    List<PizzaResponseDto> findByName(String name);

    List<PizzaResponseDto> findByIngredient(String ingredient);
}
