package com.accenture.service;

import com.accenture.service.dto.ClientPatchRequestDto;
import com.accenture.service.dto.ClientRequestDto;
import com.accenture.service.dto.ClientResponseDto;

import java.util.List;

public interface ClientService {

    /**
     * Add a client.
     * @param clientRequestDto information of client to create.
     * @return the client created in the form of clientResponseDto.
     * @throws com.accenture.exception.ClientException if data is invalid or client's mail already exist.
     */
    ClientResponseDto addClient(ClientRequestDto clientRequestDto);

    /**
     * List all clients.
     * @return list of all clients in the form of clientResponseDto.
     */
    List<ClientResponseDto> findAllClients();

    /**
     * Find client by filling his mail.
     * @param mail client's mail.
     * @return the client in the form of clientResponseDto.
     */
    ClientResponseDto findByMail(String mail);

    /**
     * Patch client by filling his mail.
     * @param mail client's mail.
     * @param clientPatchRequestDto new client's attributes.
     * @return the client in the form of clientResponseDto.
     */
    ClientResponseDto patchByMail(String mail, ClientPatchRequestDto clientPatchRequestDto);

    /**
     * Verify all client's attributes.
     * @param clientRequestDto information of client to create.
     */
    void verify(ClientRequestDto clientRequestDto);
}
