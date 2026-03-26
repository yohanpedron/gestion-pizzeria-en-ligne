package com.accenture.service.dto;

import com.accenture.model.PizzaSize;

import java.util.UUID;

public record OrderRowDescriptionResponseDto(
        UUID pizzaId,
        PizzaSize size,
        int ingredientAmount,
        double unitPrice, // prix de la pizza selon la taille
        double totalPrice
) {}

