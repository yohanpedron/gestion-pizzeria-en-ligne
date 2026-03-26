package com.accenture.service;

import com.accenture.exception.OrderException;
import com.accenture.service.dto.OrderRequestDto;
import com.accenture.service.dto.OrderResponseDto;

/**
 * Business service for managing customer orders.
 */
public interface OrderService {

    /**
     * Creates a new order.
     * @param dto data required to create the order
     * @return the created order as a DTO
     * @throws OrderException if a business rule is violated
     * @throws IllegalArgumentException if the input data is invalid
     */
    OrderResponseDto addOrder(OrderRequestDto dto) throws OrderException;
}
