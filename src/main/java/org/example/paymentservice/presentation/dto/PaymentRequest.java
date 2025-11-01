package org.example.paymentservice.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;


@Builder
@Schema(description = "Request body for creating a payment transaction")
public record PaymentRequest(

        @NotNull
        @Schema(description = "Unique identifier of the webhook to notify", 
                example = "123e4567-e89b-12d3-a456-426614174000", 
                requiredMode = Schema.RequiredMode.REQUIRED)
        UUID webhookUUID,
        
        @NotBlank
        @JsonProperty("first_name")
        @Schema(description = "Cardholder's first name", 
                example = "John", 
                requiredMode = Schema.RequiredMode.REQUIRED)
        String firstName,
        
        @NotBlank
        @JsonProperty("last_name")
        @Schema(description = "Cardholder's last name", 
                example = "Doe", 
                requiredMode = Schema.RequiredMode.REQUIRED)
        String lastName,
        
        @NotBlank
        @JsonProperty("zip_code")
        @Schema(description = "Billing zip code", 
                example = "12345", 
                requiredMode = Schema.RequiredMode.REQUIRED)
        String zipCode,
        
        @NotBlank
        @JsonProperty("card_number")
        @Schema(description = "Credit card number (will be masked in response)", 
                example = "4111111111111111", 
                requiredMode = Schema.RequiredMode.REQUIRED)
        String cardNumber) {
}