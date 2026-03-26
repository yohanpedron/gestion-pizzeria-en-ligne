package com.accenture.service.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record OrderRequestDto(
        UUID clientId,
        @NotEmpty
        List<OrderRowDescriptionRequestDto> rows
) {}
