package com.accenture.service;

import com.accenture.model.*;
import com.accenture.repository.ClientDao;
import com.accenture.repository.OrderDao;
import com.accenture.repository.PizzaDao;
import com.accenture.service.dto.OrderRequestDto;
import com.accenture.service.dto.OrderResponseDto;
import com.accenture.service.dto.OrderRowDescriptionRequestDto;
import com.accenture.service.dto.OrderRowDescriptionResponseDto;
import com.accenture.utils.Messages;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the order business logic.
 * Handles validation, price calculation and persistence.
 */
@Service
@Transactional
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private ClientDao clientDao;
    private PizzaDao pizzaDao;
    private OrderDao orderDao;

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderResponseDto addOrder(OrderRequestDto request) {

        // 1) Validations simples
        if (request.clientId() == null) {
            throw new IllegalArgumentException(Messages.CLIENT_ID_NULL);
        }
        if (request.rows() == null || request.rows().isEmpty()) {
            throw new IllegalArgumentException(Messages.ORDER_ROWS_EMPTY);
        }

        // 2) Vérifier que le client existe
        Client client = clientDao.findById(request.clientId())
                .orElseThrow(() -> new IllegalArgumentException(Messages.CLIENT_NOT_FOUND));

        // 3) Mapper les rows + calcul du total
        List<OrderRowDescription> rows = new ArrayList<>();
        double total = 0;

        for (OrderRowDescriptionRequestDto rowDto : request.rows()) {

            Pizza pizza = pizzaDao.findById(rowDto.pizzaId())
                    .orElseThrow(() -> new IllegalArgumentException(Messages.PIZZA_NOT_FOUND));

            double unitPrice = pizza.getPrice().get(rowDto.size());
            double rowTotal = unitPrice + rowDto.ingredientAmount();
            total += rowTotal;

            rows.add(new OrderRowDescription(
                    pizza,
                    rowDto.size(),
                    rowDto.ingredientAmount()
            ));
        }

        // 4) Créer la commande
        Order order = new Order(
                client,
                OrderStatus.PENDING,
                rows,
                LocalDateTime.now(),
                total
        );

        // 5) Sauvegarde
        Order saved = orderDao.save(order);

        // 6) Mapper la réponse
        return new OrderResponseDto(
                saved.getId(),
                saved.getClient().getId(),
                saved.getStatus(),
                saved.getDate(),
                saved.getTotalOrder(),
                saved.getOrderRowsDescription().stream()
                        .map(r -> new OrderRowDescriptionResponseDto(
                                r.getPizza().getId(),
                                r.getSize(),
                                r.getIngredientAmount(),
                                r.getPizza().getPrice().get(r.getSize()),
                                r.getPizza().getPrice().get(r.getSize()) + r.getIngredientAmount()
                        ))
                        .toList()
        );
    }

}

