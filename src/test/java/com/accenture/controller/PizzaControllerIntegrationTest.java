package com.accenture.controller;

import com.accenture.model.PizzaSize;
import com.accenture.service.dto.IngredientRequestDto;
import com.accenture.service.dto.PizzaRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PizzaControllerIntegrationTest {

    private static final String API_PIZZA_ENDPOINT = "/pizza";

    // Mock un serveur HTTP pour requêter sur notre serveur REST
    // C'est lui qui va appeler nos endpoints
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("TI - addPizza() must persist pizza and return 201")
    void testPersistPizzaSuccess() throws Exception {

        PizzaRequestDto requestDto = new PizzaRequestDto("mer", Map.of(PizzaSize.MEDIUM, 12.0), List.of(new IngredientRequestDto("Bolognaise"))
        );

        mockMvc.perform(post(API_PIZZA_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(MockMvcResultHandlers.print())

                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("TI - addPizza() must return 400 when name is empty")
    void testAddPizzaBadRequest() throws Exception {

        PizzaRequestDto invalidRequestName = new PizzaRequestDto(
                "", // name vide → invalide
                Map.of(PizzaSize.MEDIUM, 12.0),
                List.of(new IngredientRequestDto("Tomate"))
        );
        PizzaRequestDto invalidRequestPrice = new PizzaRequestDto(
                "Bolo",
                Map.of(), // vide
                List.of(new IngredientRequestDto("Tomate"))
        );

        PizzaRequestDto invalidRequestIngredient = new PizzaRequestDto(
                "Bolo",
                Map.of(PizzaSize.MEDIUM, 12.0),
                List.of() // vide
        );


        mockMvc.perform(post(API_PIZZA_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestName)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(API_PIZZA_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestPrice)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post(API_PIZZA_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestIngredient)))
                .andExpect(status().isBadRequest());
    }
}
