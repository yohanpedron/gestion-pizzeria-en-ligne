package com.accenture.controller.advice;

import java.time.LocalDateTime;

public record ErrorDto(LocalDateTime timestamp, int errorCode, String errorMessage) {
}
