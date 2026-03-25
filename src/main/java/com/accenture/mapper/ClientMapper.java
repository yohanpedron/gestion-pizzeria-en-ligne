package com.accenture.mapper;

import com.accenture.model.Client;
import com.accenture.service.dto.ClientRequestDto;
import com.accenture.service.dto.ClientResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    Client toClient(ClientRequestDto clientRequestDto);

    ClientResponseDto toclientResponseDto(Client client);
}
