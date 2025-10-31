package org.example.paymentservice.domain;

import org.example.paymentservice.application.command.PaymentCommand;
import org.example.paymentservice.application.command.WebhookCommand;
import org.example.paymentservice.application.service.EncryptionService;
import org.example.paymentservice.domain.model.Payment;
import org.example.paymentservice.domain.model.Webhook;
import org.example.paymentservice.presentation.dto.PaymentResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DomainMapper {

    private final EncryptionService encryptionService;

    public DomainMapper(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    public Payment toDomain(PaymentCommand command) {
        return Payment.builder()
                .id(UUID.randomUUID())
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .zipCode(command.getZipCode())
                .encryptedCardNumber(encryptionService.encrypt(command.getCardNumber()))
                .build();
    }

    public Webhook toDomain(WebhookCommand command) {
        return Webhook.builder()
                .url(command.getUrl())
                .description(command.getDescription())
                .secret(command.getSecret())
                .isActive(true)
                .build();
    }

    public PaymentResponse fromDomain(Payment paymentEntity) {
        return PaymentResponse.builder()
                .id(paymentEntity.getId())
                .firstName(paymentEntity.getFirstName())
                .lastName(paymentEntity.getLastName())
                .zipCode(paymentEntity.getZipCode())
                .maskedCardNumber(paymentEntity.getEncryptedCardNumber())
                .build();
    }
}
