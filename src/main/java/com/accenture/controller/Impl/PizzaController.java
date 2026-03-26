package com.accenture.controller.Impl;

import com.accenture.service.PizzaService;
import com.accenture.service.dto.PizzaRequestDto;
import com.accenture.service.dto.PizzaResponseDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pizza")
public class PizzaController {

    @Autowired
    private PizzaService pizzaService;

    @PostMapping
    public ResponseEntity<PizzaResponseDto> addPizza(@Valid @RequestBody PizzaRequestDto request) {
        PizzaResponseDto dto = pizzaService.addPizza(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

}

