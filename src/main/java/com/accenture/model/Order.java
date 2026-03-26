package com.accenture.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Represents a customer order.
 * An order contains a client, a list of ordered pizzas,
 * a status, a creation date and a total amount.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    /** Unique identifier of the order. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Client who placed the order. */
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    /** Current status of the order (PENDING, VALIDATED, etc.). */
    private OrderStatus status;

    /** List of pizzas included in the order. */
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private List<OrderRowDescription> orderRowsDescription;

    /** Date and time when the order was created. */
    private LocalDateTime date;

    /** Total price of the order. */
    private double totalOrder;

    public Order(Client client, OrderStatus status, List<OrderRowDescription> orderRowsDescription, LocalDateTime date, double totalOrder) {
        this.client = client;
        this.status = status;
        this.orderRowsDescription = orderRowsDescription;
        this.date = date;
        this.totalOrder = totalOrder;
    }
}
