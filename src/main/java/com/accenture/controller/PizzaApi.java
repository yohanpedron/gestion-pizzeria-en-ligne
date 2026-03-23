package com.accenture.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Pizzas", description = "Pizza's API")
@RequestMapping("/pizzas")
public interface PizzaApi {
}
