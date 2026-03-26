package com.accenture.service.dto;

import com.accenture.model.PizzaSize;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OrderRowDescriptionRequestDto(
        UUID pizzaId,
        @NotNull
        PizzaSize size,
        @Min(0)
        int ingredientAmount
) {}
