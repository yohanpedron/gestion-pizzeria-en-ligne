package com.accenture.controller;

import com.accenture.model.Client;
import com.accenture.model.Pizza;
import com.accenture.model.PizzaSize;
import com.accenture.repository.ClientDao;
import com.accenture.repository.OrderDao;
import com.accenture.repository.PizzaDao;
import com.accenture.service.dto.OrderRequestDto;
import com.accenture.service.dto.OrderResponseDto;
import com.accenture.service.dto.OrderRowDescriptionRequestDto;
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

import java.util.ArrayList;
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
class OrderControllerIntegrationEndToEndTest {

    private static final String API_ORDER_ENDPOINT = "/orders";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ClientDao clientDao;

    @Autowired
    private PizzaDao pizzaDao;

    @Autowired
    private OrderDao orderDao;

    @Test
    @DisplayName("EndToEnd - addOrder() should persist order and return 201")
    void testAddOrderSuccess() {

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

        // 3) Construire la requête
        OrderRequestDto requestDto = new OrderRequestDto(
                client.getId(),
                List.of(new OrderRowDescriptionRequestDto(
                        pizza.getId(),
                        PizzaSize.MEDIUM,
                        0
                ))
        );

        // 4) Appeler l’endpoint
        ResponseEntity<OrderResponseDto> response = restTemplate.postForEntity(
                "http://localhost:" + port + API_ORDER_ENDPOINT,
                requestDto,
                OrderResponseDto.class
        );

        // 5) Vérifications HTTP
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // 6) Vérifications du JSON retourné
        OrderResponseDto body = response.getBody();
        Assertions.assertNotNull(body);
        Assertions.assertEquals(client.getId(), body.clientId());
        Assertions.assertEquals(10.0, body.totalOrder());
        Assertions.assertEquals(1, body.rows().size());
        Assertions.assertEquals(pizza.getId(), body.rows().getFirst().pizzaId());

        // 7) Vérifier que la commande est bien persistée en base
        Assertions.assertTrue(orderDao.findById(body.id()).isPresent());
    }
}
