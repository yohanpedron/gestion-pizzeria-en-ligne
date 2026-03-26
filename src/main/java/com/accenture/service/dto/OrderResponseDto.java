package com.accenture.service.dto;

import com.accenture.model.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponseDto(
        UUID id,
        UUID clientId,
        OrderStatus status,
        LocalDateTime date,
        double totalOrder,
        List<OrderRowDescriptionResponseDto> rows
) {}
