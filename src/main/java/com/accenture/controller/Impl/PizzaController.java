package com.accenture.controller.Impl;

import com.accenture.service.PizzaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class PizzaController {

    private final PizzaService pizzaService;
}
