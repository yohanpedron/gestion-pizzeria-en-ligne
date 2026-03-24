package com.accenture.mapper;

import com.accenture.model.Order;
import com.accenture.service.dto.OrderRequestDto;
import com.accenture.service.dto.OrderResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    Order toOrder(OrderRequestDto orderRequestDto);

    OrderResponseDto toOrderResponseDto(Order order);
}
