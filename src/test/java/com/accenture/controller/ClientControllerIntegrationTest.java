package com.accenture.controller;

import com.accenture.controller.impl.ClientController;
import com.accenture.exception.ClientException;
import com.accenture.mapper.ClientMapper;
import com.accenture.model.Order;
import com.accenture.service.ClientServiceImpl;
import com.accenture.service.dto.ClientRequestDto;
import com.accenture.service.dto.ClientResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebMvcTest(controllers = ClientController.class)
class ClientControllerIntegrationTest {

    private static final String API_CLIENTS_ENDPOINT = "/clients";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClientServiceImpl clientService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClientMapper clientMapper;

    @Test
    @DisplayName("Test to persist client into the postgres database")
    void testPersistClientSuccess() throws Exception{
        String name = "Name";
        String mail = "mail@mail.com";

        ClientRequestDto jsonBody = new ClientRequestDto(name,mail);

        List<Order> orders = new ArrayList<>();
        boolean vip = false;

        ClientResponseDto clientResponseDto = new ClientResponseDto(UUID.randomUUID(),name,mail,orders,vip);

        Mockito.when(clientService.addClient(Mockito.any(ClientRequestDto.class))).thenReturn(clientResponseDto);

        mockMvc.perform(MockMvcRequestBuilders.post(API_CLIENTS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(jsonBody)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @DisplayName("Test to persist client with invalid name and return bad request")
    void testPersistClientInvalidNameBadRequest() throws Exception{
        String name = " ";
        String mail = "mail@mail.com";

        ClientRequestDto jsonBody = new ClientRequestDto(name,mail);

        Mockito.when(clientService.addClient(Mockito.any(ClientRequestDto.class))).thenThrow(ClientException.class);

        mockMvc.perform(MockMvcRequestBuilders.post(API_CLIENTS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(jsonBody)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Test to persist client with invalid mail and return bad request")
    void testPersistClientInvalidMailBadRequest() throws Exception{
        String name = "Name";
        String mail = "mailmail.com";

        ClientRequestDto jsonBody = new ClientRequestDto(name,mail);

        Mockito.when(clientService.addClient(Mockito.any(ClientRequestDto.class))).thenThrow(ClientException.class);

        mockMvc.perform(MockMvcRequestBuilders.post(API_CLIENTS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(jsonBody)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
