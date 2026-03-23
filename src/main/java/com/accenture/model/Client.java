package com.accenture.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String mail;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn
    private List<Order> orders;
    private boolean vip;

    public Client(String name, String mail, List<Order> orders, boolean vip) {
        this.name = name;
        this.mail = mail;
        this.orders = orders;
        this.vip = vip;
    }
}
