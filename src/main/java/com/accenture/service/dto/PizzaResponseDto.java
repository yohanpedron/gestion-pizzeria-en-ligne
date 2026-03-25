package com.accenture.service.dto;

import com.accenture.model.PizzaSize;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record PizzaResponseDto(
        UUID id,
        String name,
        Map<PizzaSize, Double> price,
        List<IngredientResponseDto> ingredients,
        boolean active
) {}
