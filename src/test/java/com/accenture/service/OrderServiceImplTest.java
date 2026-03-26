package com.accenture.service;

import com.accenture.model.*;
import com.accenture.repository.ClientDao;
import com.accenture.repository.OrderDao;
import com.accenture.repository.PizzaDao;
import com.accenture.service.dto.OrderRequestDto;
import com.accenture.service.dto.OrderResponseDto;
import com.accenture.service.dto.OrderRowDescriptionRequestDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderDao orderDao;

    @Mock
    private ClientDao clientDao;

    @Mock
    private PizzaDao pizzaDao;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        //MockitoExtension + InjectMocks s'occupent de tout
    }

    @Test
    @DisplayName("addOrder() should create order successfully")
    void testAddOrderSuccess() {

        UUID clientId = UUID.randomUUID();
        UUID pizzaId = UUID.randomUUID();

        OrderRequestDto request = new OrderRequestDto(
                clientId,
                List.of(new OrderRowDescriptionRequestDto(pizzaId, PizzaSize.MEDIUM, 2))
        );

        Client client = new Client("John", "john@mail.com", new ArrayList<>(), false);
        client.setId(clientId);

        Pizza pizza = new Pizza(
                "Bolo",
                Map.of(PizzaSize.MEDIUM, 10.0),
                new ArrayList<>(),   // liste d’ingrédients vide
                true
        );
        pizza.setId(pizzaId); // ajout ID manuellement pour le test


        Mockito.when(clientDao.findById(clientId)).thenReturn(Optional.of(client));
        Mockito.when(pizzaDao.findById(pizzaId)).thenReturn(Optional.of(pizza));

        Mockito.when(orderDao.save(Mockito.any(Order.class)))
                .thenAnswer(invocation -> {
                    Order o = invocation.getArgument(0);
                    o.setId(UUID.randomUUID());
                    return o;
                });


        OrderResponseDto response = orderService.addOrder(request);

        Mockito.verify(orderDao, Mockito.times(1)).save(Mockito.any());


        Assertions.assertNotNull(response.id());
        Assertions.assertEquals(clientId, response.clientId());
        Assertions.assertEquals(OrderStatus.PENDING, response.status());
        Assertions.assertEquals(1, response.rows().size());
        Assertions.assertEquals(10.0 + 2, response.totalOrder()); // prix + ingrédients
    }

    @Test
    @DisplayName("addOrder() should throw when client does not exist")
    void testAddOrderClientNotFound() {
        UUID clientId = UUID.randomUUID();

        OrderRequestDto request = new OrderRequestDto(
                clientId,
                List.of(new OrderRowDescriptionRequestDto(UUID.randomUUID(), PizzaSize.MEDIUM, 1))
        );

        Mockito.when(clientDao.findById(clientId)).thenReturn(Optional.empty());

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> orderService.addOrder(request));
    }

    @Test
    @DisplayName("addOrder() should throw when pizza does not exist")
    void testAddOrderPizzaNotFound() {
        UUID clientId = UUID.randomUUID();
        UUID pizzaId = UUID.randomUUID();

        OrderRequestDto request = new OrderRequestDto(
                clientId,
                List.of(new OrderRowDescriptionRequestDto(pizzaId, PizzaSize.MEDIUM, 1))
        );

        Mockito.when(clientDao.findById(clientId)).thenReturn(Optional.of(new Client()));
        Mockito.when(pizzaDao.findById(pizzaId)).thenReturn(Optional.empty());

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> orderService.addOrder(request));
    }

    @Test
    @DisplayName("addOrder() should throw when rows are empty")
    void testAddOrderRowsEmpty() {
        OrderRequestDto request = new OrderRequestDto(
                UUID.randomUUID(),
                List.of()
        );

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> orderService.addOrder(request));
    }

}
