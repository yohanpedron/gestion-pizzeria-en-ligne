package com.accenture.mapper;

import com.accenture.model.Pizza;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface Pizzamapper {

    Pizza toPizzaDto(PizzaRequestDto pizzaRequestDto);

    PizzaResponseDto toPizzaResponseDto(Pizza pizza);
}
