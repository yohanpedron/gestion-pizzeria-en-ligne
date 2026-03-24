package com.accenture.service;

import com.accenture.exception.ClientException;
import com.accenture.mapper.ClientMapper;
import com.accenture.model.Client;
import com.accenture.repository.ClientDao;
import com.accenture.service.dto.ClientRequestDto;
import com.accenture.service.dto.ClientResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class ClientServiceImplMockitoTest {

    @Mock
    private ClientDao clientDao;
    @Mock
    private ClientMapper clientMapper;
    @Mock
    private OrderService orderService;
    @Mock
    private MessageSourceAccessor messages;

    private ClientService service;

    @BeforeEach
    void setUp(){
        clientDao = mock(ClientDao.class);
        clientMapper = mock(ClientMapper.class);
        orderService = mock(OrderService.class);
        messages = mock(MessageSourceAccessor.class);
        service = new ClientServiceImpl(clientDao, clientMapper, orderService, messages);
    }

    @Test
    @DisplayName("Test when client object is well persisted from valid input")
    void testAddClientSuccess(){
        ClientService spy = Mockito.spy(service);
        String name = "Name1";
        String mail = "mail@mail.com";

        ClientRequestDto clientRequestDto = new ClientRequestDto(name,mail);
        ClientResponseDto clientResponseDto = new ClientResponseDto(UUID.randomUUID(),name,mail,new ArrayList<>(),false);

        Client clientEntity = new Client(name,mail,new ArrayList<>(),false);

        Mockito.when(clientMapper.toClient(Mockito.any(ClientRequestDto.class))).thenReturn(clientEntity);
        Mockito.when(clientDao.save(Mockito.any(Client.class))).thenReturn(clientEntity);
        Mockito.when(clientMapper.toclientResponseDto(Mockito.any(Client.class))).thenReturn(clientResponseDto);

        ClientResponseDto returnedValue = spy.addClient(clientRequestDto);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(returnedValue, "ReturnedValue should not be null"),
                () -> Assertions.assertNotNull(returnedValue.id(), "Id should not be null"),
                () -> Assertions.assertNotNull(returnedValue.name(), "Name should not be null"),
                () -> Assertions.assertNotNull(returnedValue.mail(), "Mail should not be null"),
                () -> Assertions.assertNotNull(returnedValue.orders(), "Orders should not be null"),
                () -> Assertions.assertTrue(returnedValue.orders().isEmpty(), "Orders should be empty"),
                () -> Assertions.assertFalse(returnedValue.vip(), "Name should not be true")
        );
        Mockito.verify(spy, Mockito.times(1)).verify(Mockito.any(ClientRequestDto.class));
    }

    @Test
    @DisplayName("Test when client is null and return ClientException")
    void testAddClientNullFail(){
        Assertions.assertThrows(ClientException.class, () -> service.addClient(null));
    }

    @Test
    @DisplayName("Test when client name is blank and return ClientException")
    void testAddClientNameBlankFail(){
        String name = " ";
        String mail = "mail@mail.com";
        ClientRequestDto clientRequestDto = new ClientRequestDto(name,mail);
        Assertions.assertThrows(ClientException.class, () -> service.addClient(clientRequestDto));
    }

    @Test
    @DisplayName("Test when client mail is blank and return ClientException")
    void testAddClientMailBlankFail(){
        String name = "Name1";
        String mail = " ";
        ClientRequestDto clientRequestDto = new ClientRequestDto(name,mail);
        Assertions.assertThrows(ClientException.class, () -> service.addClient(clientRequestDto));
    }

    @Test
    @DisplayName("Test when client mail is in wrong format and return ClientException")
    void testAddClientMailWrongFormatFail(){
        String name = "Name1";
        String mail = "mailmail.com";
        ClientRequestDto clientRequestDto = new ClientRequestDto(name,mail);
        Assertions.assertThrows(ClientException.class, () -> service.addClient(clientRequestDto));
    }

    @Test
    @DisplayName("Test the methode findAll from service, must return correct output")
    void testFindAllClientSuccess(){

        Client client1 = new Client("Name1","mail1@mail.com",new ArrayList<>(),false);
        Client client2 = new Client("Name2","mail2@mail.com",new ArrayList<>(),false);
        List<Client> clients = List.of(client1,client2);

        Mockito.when(clientDao.findAll()).thenReturn(clients);

        List<Client> result = service.findAllClients();

        Assertions.assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Test the methode findAll from service, must return empty list")
    void testFindAllClientEmptyListSuccess(){

        List<Client> clients = new ArrayList<>();
        Mockito.when(clientDao.findAll()).thenReturn(clients);

        List<Client> result = service.findAllClients();

        Assertions.assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Test when client object is delete with success")
    void testDeleteClientSuccess(){
        ClientService spy = Mockito.spy(service);
        String name = "Name1";
        String mail = "mail@mail.com";

        ClientRequestDto clientRequestDto = new ClientRequestDto(name,mail);
        ClientResponseDto clientResponseDto = new ClientResponseDto(UUID.randomUUID(),name,mail,new ArrayList<>(),false);

        Client clientEntity = new Client(name,mail,new ArrayList<>(),false);

        Mockito.when(clientMapper.toClient(Mockito.any(ClientRequestDto.class))).thenReturn(clientEntity);
        Mockito.when(clientDao.save(Mockito.any(Client.class))).thenReturn(clientEntity);
        Mockito.when(clientMapper.toclientResponseDto(Mockito.any(Client.class))).thenReturn(clientResponseDto);

        ClientResponseDto returnedValue = spy.addClient(clientRequestDto);
        UUID id = UUID.randomUUID();
        clientDao.save(new Client(id,"Name1","mail1@mail.com",new ArrayList<>(),false));

        service.delete(id);
        Mockito.verify(spy, Mockito.times(0)).verify(Mockito.any(ClientRequestDto.class));
    }



}
