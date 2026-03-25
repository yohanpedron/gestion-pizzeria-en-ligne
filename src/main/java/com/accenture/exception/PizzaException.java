package com.accenture.exception;

import com.accenture.service.dto.PizzaRequestDto;

public class PizzaException extends RuntimeException {
    public PizzaException(String message) {
        super(message);
    }
}
