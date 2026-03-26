package com.accenture.controller;

import com.accenture.model.Client;
import com.accenture.model.Pizza;
import com.accenture.model.PizzaSize;
import com.accenture.repository.ClientDao;
import com.accenture.repository.PizzaDao;
import com.accenture.service.dto.OrderRequestDto;
import com.accenture.service.dto.OrderRowDescriptionRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import tools.jackson.databind.ObjectMapper;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

    private static final String API_ORDER_ENDPOINT = "/orders";

    // Mock un serveur HTTP pour requêter sur notre serveur REST
    // C'est lui qui va appeler nos endpoints
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClientDao clientDao;

    @Autowired
    private PizzaDao pizzaDao;

    @Test
    @DisplayName("TI - addOrder() must persist order and return 201")
    void testAddOrderSuccess() throws Exception {

        // 1) Préparer un client en base
        Client client = new Client("John", "john@mail.com", new ArrayList<>(), true);
        client = clientDao.save(client);

        // 2) Préparer une pizza en base
        Pizza pizza = new Pizza(
                "Bolo",
                Map.of(PizzaSize.MEDIUM, 10.0),
                List.of(),
                true
        );
        pizza = pizzaDao.save(pizza);

        // 3) Construire la requête JSON
        OrderRequestDto requestDto = new OrderRequestDto(
                client.getId(),
                List.of(new OrderRowDescriptionRequestDto(
                        pizza.getId(),
                        PizzaSize.MEDIUM,
                        2
                ))
        );

        mockMvc.perform(post(API_ORDER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.clientId").value(client.getId().toString()))
                .andExpect(jsonPath("$.rows").isArray());
    }

    @Test
    @DisplayName("TI - addOrder() must return 400 when client does not exist")
    void testAddOrderClientNotFound() throws Exception {

        OrderRequestDto requestDto = new OrderRequestDto(
                UUID.randomUUID(), // client inexistant
                List.of(new OrderRowDescriptionRequestDto(
                        UUID.randomUUID(),
                        PizzaSize.MEDIUM,
                        1
                ))
        );

        mockMvc.perform(post(API_ORDER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TI - addOrder() must return 400 when rows are empty")
    void testAddOrderRowsEmpty() throws Exception {

        OrderRequestDto requestDto = new OrderRequestDto(
                UUID.randomUUID(),
                List.of() // rows vide
        );

        mockMvc.perform(post(API_ORDER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }
}