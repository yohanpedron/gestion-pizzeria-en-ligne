package com.accenture.service;

import com.accenture.model.Client;
import com.accenture.service.dto.ClientRequestDto;
import com.accenture.service.dto.ClientResponseDto;

import java.util.List;
import java.util.UUID;

public interface ClientService {
    ClientResponseDto addClient(ClientRequestDto clientRequestDto);
    List<Client> findAllClients();
    void verify(ClientRequestDto clientRequestDto);

    void delete(UUID id);
}
