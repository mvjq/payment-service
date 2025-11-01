package org.example.paymentservice.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response body containing payment transaction details")
public class PaymentResponse {

    @JsonProperty("id")
    @Schema(description = "Unique identifier of the payment transaction", 
            example = "987e6543-e21b-12d3-a456-426614174111")
    private UUID id;

    @JsonProperty("first_name")
    @Schema(description = "Cardholder's first name", 
            example = "John")
    private String firstName;

    @JsonProperty("last_name")
    @Schema(description = "Cardholder's last name", 
            example = "Doe")
    private String lastName;

    @JsonProperty("zip_code")
    @Schema(description = "Billing zip code", 
            example = "12345")
    private String zipCode;

    @JsonProperty("masked_card_number")
    @Schema(description = "Masked credit card number for security", 
            example = "************1111")
    private String maskedCardNumber;

    @JsonProperty("created_at")
    @Schema(description = "Timestamp when the payment was created", 
            example = "2025-10-31T10:15:30")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @Schema(description = "Timestamp when the payment was last updated", 
            example = "2025-10-31T10:15:30")
    private LocalDateTime updatedAt;


}