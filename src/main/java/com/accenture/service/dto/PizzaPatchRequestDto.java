package com.accenture.service.dto;

import com.accenture.model.PizzaSize;

import java.util.List;
import java.util.Map;

public record PizzaPatchRequestDto(
        String name,
        Map<PizzaSize, Double> price,
        List<IngredientRequestDto> ingredients
) { }
