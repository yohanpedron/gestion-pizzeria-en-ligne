package com.accenture.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    private OrderStatus status;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn
    private List<OrderRowDescription> orderRowsDescription;

    private LocalDateTime date;
    private double totalOrder;

    public Order(Client client, OrderStatus status, List<OrderRowDescription> orderRowsDescription, LocalDateTime date, double totalOrder) {
        this.client = client;
        this.status = status;
        this.orderRowsDescription = orderRowsDescription;
        this.date = date;
        this.totalOrder = totalOrder;
    }
}
