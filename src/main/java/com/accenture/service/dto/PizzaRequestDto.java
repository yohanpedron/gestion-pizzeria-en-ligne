package com.accenture.service.dto;

import com.accenture.model.PizzaSize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Map;

public record PizzaRequestDto(

        String name,

        Map<PizzaSize, Double> price,

        List<IngredientRequestDto> ingredients

        //boolean active
)
{}
