package com.accenture.repository;

import com.accenture.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientDao extends JpaRepository<Client, UUID> {
}
