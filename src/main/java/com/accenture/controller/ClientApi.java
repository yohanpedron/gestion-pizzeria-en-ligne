package com.accenture.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Clients", description = "Client's API")
@RequestMapping("/clients")
public interface ClientApi {
}
