package org.example.paymentservice.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.paymentservice.application.command.PaymentCommand;


@Builder
public record PaymentRequest (
    @NotBlank
    @JsonProperty("first_name")
    String firstName,
    @NotBlank
    @JsonProperty("last_name")
    String lastName,
    @NotBlank
    @JsonProperty("zip_code")
    String zipCode,
    @NotBlank
    @JsonProperty("card_number")
    String cardNumber) {}