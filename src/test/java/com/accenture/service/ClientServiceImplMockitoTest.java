package com.accenture.service;

import com.accenture.exception.ClientException;
import com.accenture.mapper.ClientMapper;
import com.accenture.model.Client;
import com.accenture.model.Order;
import com.accenture.repository.ClientDao;
import com.accenture.service.dto.ClientRequestDto;
import com.accenture.service.dto.ClientResponseDto;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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
        Mockito.when(clientMapper.toClientResponseDto(Mockito.any(Client.class))).thenReturn(clientResponseDto);

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
    @DisplayName("Test when client mail already exist and return ClientException")
    void testAddClientMailAlreadyExist(){

        String name = "Name";
        String mail = "mail@mail.com";

        ClientRequestDto clientRequestDto = new ClientRequestDto(name,mail);

        Mockito.when(clientDao.findByMail(eq(mail))).thenReturn(Optional.of(new Client()));

        Assertions.assertThrows(ClientException.class, () -> service.addClient(clientRequestDto));

        Mockito.verify(clientDao, Mockito.times(1)).findByMail(eq(mail));

        Mockito.verifyNoInteractions(clientMapper);
        Mockito.verify(clientDao, never()).save(any(Client.class));
        Mockito.verifyNoMoreInteractions(clientDao);

    }

    @Test
    @DisplayName("Test the methode findAll from service, must return correct output")
    void testFindAllClientSuccess(){

        Client client1 = new Client("Name1","mail1@mail.com",new ArrayList<>(),false);
        Client client2 = new Client("Name2","mail2@mail.com",new ArrayList<>(),false);
        List<Client> clients = List.of(client1,client2);

        Mockito.when(clientDao.findAll()).thenReturn(clients);

        List<ClientResponseDto> result = service.findAllClients();

        Assertions.assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Test the methode findAll from service, must return empty list")
    void testFindAllClientEmptyListSuccess(){

        List<Client> clients = new ArrayList<>();
        Mockito.when(clientDao.findAll()).thenReturn(clients);

        List<ClientResponseDto> result = service.findAllClients();

        Assertions.assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Test findByMail from service, must return correct output")
    void testFindByMailClientSuccess(){
        UUID idClient1 = UUID.randomUUID();
        String mail = "mail1@mail.com";
        Client client1 = new Client(idClient1,"Name1",mail,new ArrayList<>(),false);

        ClientResponseDto clientResponseDto1 = new ClientResponseDto(client1.getId(),client1.getName(),client1.getMail(),client1.getOrders(),client1.isVip());

        Mockito.when(clientMapper.toClientResponseDto(Mockito.any(Client.class))).thenReturn(clientResponseDto1);
        Mockito.when(clientDao.findByMail(mail)).thenReturn(Optional.of(client1));

        ClientResponseDto result = service.findByMail(mail);

        Assertions.assertAll(
                () -> Assertions.assertEquals(clientResponseDto1,result),
                () -> Assertions.assertEquals(clientResponseDto1.id(),result.id()),
                () -> Assertions.assertEquals(clientResponseDto1.name(),result.name()),
                () -> Assertions.assertEquals(clientResponseDto1.mail(),result.mail()),
                () -> Assertions.assertEquals(clientResponseDto1.orders(),result.orders()),
                () -> Assertions.assertEquals(clientResponseDto1.vip(),result.vip())
                );
    }

    @Test
    @DisplayName("Test findByMail from service, must return exception")
    void testFindByMailClientNotFound(){
        String mail = "mail@mail.com";
        Mockito.when(clientDao.findByMail(mail)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class,() -> service.findByMail(mail));
    }

    @Test
    @DisplayName("Test patch from service, must return correct output")
    void testPatchByMailClientSuccess(){

        // Arrange
        String mail = "client@mail.com";
        UUID id = UUID.randomUUID();
        List<Order> orders = new ArrayList<>();
        boolean vip = false;

        // Client in BDD
        Client existing = new Client(id,"OldName",mail,orders,vip);

        // Patch
        String newName = "NewName";

        // Après patch + save
        Client saved = new Client(id,newName,mail,orders,vip);

        ClientResponseDto clientResponseDto = new ClientResponseDto(id,newName,mail,orders,vip);

        Mockito.when(clientDao.findByMail(mail)).thenReturn(Optional.of(existing));
        Mockito.when(clientDao.save(saved)).thenReturn(saved);
        Mockito.when(clientMapper.toClientResponseDto(saved)).thenReturn(clientResponseDto);

        ClientResponseDto result = service.patchByMail(mail, newName);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals(id,result.id()),
                () -> Assertions.assertEquals(newName,result.name()),
                () -> Assertions.assertEquals(mail,result.mail()),
                () -> Assertions.assertEquals(orders,result.orders()),
                () -> Assertions.assertEquals(vip,result.vip())
        );

        Mockito.verify(clientDao).findByMail(mail);
        Mockito.verify(clientMapper).toClientResponseDto(existing);
        Mockito.verifyNoMoreInteractions(clientDao,clientMapper);
    }

    @Test
    @DisplayName("Test patch from service, must return old name")
    void testPatchByMailClientNameInvalidFail(){

        // Arrange
        String mail = "client@mail.com";
        UUID id = UUID.randomUUID();
        List<Order> orders = new ArrayList<>();
        boolean vip = false;
        String oldName = "OldName";

        // Client in BDD
        Client existing = new Client(id,oldName,mail,orders,vip);

        // Patch
        String newName = " ";

        // Après patch + save
        Client saved = new Client(id,oldName,mail,orders,vip);

        ClientResponseDto clientResponseDto = new ClientResponseDto(id,oldName,mail,orders,vip);

        Mockito.when(clientDao.findByMail(mail)).thenReturn(Optional.of(existing));
        Mockito.when(clientDao.save(saved)).thenReturn(saved);
        Mockito.when(clientMapper.toClientResponseDto(saved)).thenReturn(clientResponseDto);

        ClientResponseDto result = service.patchByMail(mail, newName);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(result),
                () -> Assertions.assertEquals(id,result.id()),
                () -> Assertions.assertEquals(oldName,result.name()),
                () -> Assertions.assertEquals(mail,result.mail()),
                () -> Assertions.assertEquals(orders,result.orders()),
                () -> Assertions.assertEquals(vip,result.vip())
        );

        Mockito.verify(clientDao).findByMail(mail);
        Mockito.verify(clientMapper).toClientResponseDto(existing);
        Mockito.verifyNoMoreInteractions(clientDao,clientMapper);
    }

    @Test
    @DisplayName("Test patch from service, must return old name")
    void testPatchByMailClientNotFoundFail() {
        String mail = "mail@mail.com";
        Mockito.when(clientDao.findByMail(mail)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class,() -> service.patchByMail(mail,"Name"));
    }

}
