package com.accenture.mapper;

import com.accenture.model.Client;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    Client toClientDto(ClientRequestDto clientRequestDto);

    ClientResponseDto toclientResponseDto(Client client);
}
