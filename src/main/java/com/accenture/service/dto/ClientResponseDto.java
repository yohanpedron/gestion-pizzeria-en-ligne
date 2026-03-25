package com.accenture.service.dto;

import com.accenture.model.Order;

import java.util.List;
import java.util.UUID;

public record ClientResponseDto(
        UUID id,
        String name,
        String mail,
        List<Order> orders,
        boolean vip
) {
}
