package com.accenture.repository;

import com.accenture.model.Pizza;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PizzaDao extends JpaRepository<Pizza, UUID> {
}
