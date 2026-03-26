package com.accenture.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Entity which represent a client.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Client {

    /**
     * Client's unique identifier.
     * This identifier is generated as UUID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Client's name.
     */
    private String name;

    /**
     * Client's email address.
     */
    private String mail;

    /**
     * Client's orders.
     * Client's orders is generated as an empty list when the client is created.
     */
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn
    private List<Order> orders;

    /**
     * Client's vip status.
     * Client's vip is generated as false when the client is created.
     */
    private boolean vip;

    public Client(String name, String mail, List<Order> orders, boolean vip) {
        this.name = name;
        this.mail = mail;
        this.orders = orders;
        this.vip = vip;
    }
}
