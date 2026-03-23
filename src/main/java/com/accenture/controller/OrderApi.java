package com.accenture.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Orders", description = "Order's API")
@RequestMapping("/orders")
public interface OrderApi {
}
