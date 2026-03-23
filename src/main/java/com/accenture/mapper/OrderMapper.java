package com.accenture.mapper;

import com.accenture.model.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface Ordermapper {

    Order toOrderDto(OrderRequestDto orderRequestDto);

    OrderResponseDto toOrderResponseDto(Order order);
}
