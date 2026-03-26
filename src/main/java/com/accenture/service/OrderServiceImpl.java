package com.accenture.service;

import com.accenture.model.*;
import com.accenture.repository.ClientDao;
import com.accenture.repository.OrderDao;
import com.accenture.repository.PizzaDao;
import com.accenture.service.dto.OrderRequestDto;
import com.accenture.service.dto.OrderResponseDto;
import com.accenture.service.dto.OrderRowDescriptionRequestDto;
import com.accenture.service.dto.OrderRowDescriptionResponseDto;
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
            throw new IllegalArgumentException("Client ID cannot be null");
        }
        if (request.rows() == null || request.rows().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one row");
        }

        // 2) Vérifier que le client existe
        Client client = clientDao.findById(request.clientId())
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        // 3) Mapper les rows + calcul du total
        List<OrderRowDescription> rows = new ArrayList<>();
        double total = 0;

        for (OrderRowDescriptionRequestDto rowDto : request.rows()) {

            Pizza pizza = pizzaDao.findById(rowDto.pizzaId())
                    .orElseThrow(() -> new IllegalArgumentException("Pizza not found"));

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

