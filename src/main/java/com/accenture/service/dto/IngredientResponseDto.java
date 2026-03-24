package com.accenture.service.dto;

import java.util.UUID;

public record IngredientResponseDto(
        UUID id,
        String name
) {
}
