package com.accenture.service.dto;

import com.accenture.model.PizzaSize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Map;

public record PizzaRequestDto(
        @NotBlank
        String name,

        @NotEmpty
        Map<PizzaSize, Double> price,

        @NotEmpty
        List<IngredientRequestDto> ingredients,

        boolean active)
{}
