package com.accenture.mapper;

import com.accenture.model.Client;
import com.accenture.model.Pizza;
import com.accenture.service.dto.PizzaRequestDto;
import com.accenture.service.dto.PizzaResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PizzaMapper {

    Pizza toPizzaDto(PizzaRequestDto pizzaRequestDto);

    PizzaResponseDto toPizzaResponseDto(Pizza pizza);
}
