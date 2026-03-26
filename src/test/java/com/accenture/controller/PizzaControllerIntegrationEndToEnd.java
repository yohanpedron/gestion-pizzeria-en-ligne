package com.accenture.controller;

import com.accenture.model.PizzaSize;
import com.accenture.service.dto.IngredientRequestDto;
import com.accenture.service.dto.PizzaRequestDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

// on lance l'appli spring dans un contexte de test pour ce endpoint en particulier
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// permet à la config du serveur émetteur de la requete avec le serveur receveur selon les
//normes REST
@AutoConfigureTestRestTemplate
//identifie un context d'éxecution de test avec les fichiers de config associé
// ex: application-test.yml qui va charger le driver H2 et non le driver Postgres courant/nominal
@ActiveProfiles("test")
class PizzaControllerIntegrationEndToEndTest {

    private static final String API_PIZZA_ENDPOINT = "/pizzas";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Creates pizza through Post endpoint")
    void testPostPizzaSuccess() {
        PizzaRequestDto pizzaRequestDto = new PizzaRequestDto("Savoyarde", Map.of(PizzaSize.LARGE, 14.0), List.of(new IngredientRequestDto("Reblochon")));

        ResponseEntity<Void> response = restTemplate.postForEntity("http://localhost:"+port+API_PIZZA_ENDPOINT, pizzaRequestDto, Void.class);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

}
