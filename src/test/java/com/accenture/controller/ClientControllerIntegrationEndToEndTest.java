package com.accenture.controller;

import com.accenture.service.ClientServiceImpl;
import com.accenture.service.dto.ClientRequestDto;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
class ClientControllerIntegrationEndToEndTest {

    private static String API_CLIENTS_ENDPOINT = "/clients";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Create displate through Post endpoint")
    void testPostClientSuccess(){
        ClientRequestDto clientRequestDto = new ClientRequestDto("Name","mail@mail.com");

        ResponseEntity<Void> response = restTemplate.postForEntity("http://localhost:" + port + API_CLIENTS_ENDPOINT, clientRequestDto, Void.class);
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
}
