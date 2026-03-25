package com.accenture.service;

import com.accenture.exception.ClientException;
import com.accenture.mapper.ClientMapper;
import com.accenture.model.Client;
import com.accenture.repository.ClientDao;
import com.accenture.service.dto.ClientRequestDto;
import com.accenture.service.dto.ClientResponseDto;
import com.accenture.utils.Messages;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService{

    private final ClientDao clientDao;
    private final ClientMapper clientMapper;
    private final OrderService orderService;
    private final MessageSourceAccessor messages;

    @Override
    public ClientResponseDto addClient(ClientRequestDto clientRequestDto) throws ClientException{
        verify(clientRequestDto);
        Client saved = clientDao.save(clientMapper.toClient(clientRequestDto));
        return clientMapper.toclientResponseDto(saved);
    }

    @Override
    public List<Client> findAllClients() {
        List<Client> clients = clientDao.findAll();
        return clients.stream().toList();
    }

    @Override
    public void delete(UUID id) {

    }

    public void verify(ClientRequestDto clientRequestDto) {
        if (clientRequestDto == null)
            throw new ClientException(messages.getMessage(Messages.CLIENT_NULL));
        if (clientRequestDto.name() == null || clientRequestDto.name().isBlank())
            throw new ClientException(messages.getMessage(Messages.CLIENT_NAME_NULLORBLANK));
        if (clientRequestDto.mail() == null || clientRequestDto.mail().isBlank())
            throw new ClientException(messages.getMessage(Messages.CLIENT_MAIL_NULLORBLANK));
        if (!Pattern.matches("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$",clientRequestDto.mail()))
            throw new ClientException(messages.getMessage(Messages.CLIENT_MAIL_WRONGFORMAT));
        for(int compteur = 0;compteur < findAllClients().size();compteur++) {
            if (findAllClients().get(compteur).getMail().equals(clientRequestDto.mail()))
                throw new ClientException(messages.getMessage(Messages.CLIENT_MAIL_ALREADYEXIST));
        }
    }




}
