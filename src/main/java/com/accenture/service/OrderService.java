package com.accenture.service;

import com.accenture.service.dto.OrderRequestDto;
import com.accenture.service.dto.OrderResponseDto;

public interface OrderService {
    OrderResponseDto addOrder(OrderRequestDto dto) throws Exception;
}
