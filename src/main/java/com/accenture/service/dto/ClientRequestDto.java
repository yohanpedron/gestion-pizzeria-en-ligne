package com.accenture.service.dto;

import com.accenture.model.Order;
import com.accenture.utils.Messages;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.UUID;

public record ClientRequestDto(
        String name,
        String mail
) {
}
