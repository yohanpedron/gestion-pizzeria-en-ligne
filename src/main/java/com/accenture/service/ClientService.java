package com.accenture.service;

import com.accenture.service.dto.ClientRequestDto;
import com.accenture.service.dto.ClientResponseDto;

import java.util.List;
import java.util.UUID;

public interface ClientService {
    ClientResponseDto addClient(ClientRequestDto clientRequestDto);
    List<ClientResponseDto> findAllClients();
    void verify(ClientRequestDto clientRequestDto);

    void deleteClient(UUID id);

    ClientResponseDto findByMail(String mail);

    ClientResponseDto patchByMail(String mail, String newName);
}
