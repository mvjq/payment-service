package org.example.paymentservice.domain.model;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    private UUID id;
    private String firstName;
    private String lastName;
    private String zipCode;
    private String encryptedCardNumber;
}
