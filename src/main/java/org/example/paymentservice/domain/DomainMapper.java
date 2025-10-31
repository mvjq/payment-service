package org.example.paymentservice.domain;

import org.example.paymentservice.application.command.PaymentCommand;
import org.example.paymentservice.application.command.WebhookCommand;
import org.example.paymentservice.application.service.EncryptionService;
import org.example.paymentservice.domain.model.Payment;
import org.example.paymentservice.domain.model.Webhook;
import org.example.paymentservice.infrastructure.adapter.persistence.entity.PaymentEntity;
import org.example.paymentservice.infrastructure.adapter.persistence.entity.WebhookEntity;
import org.example.paymentservice.presentation.dto.PaymentResponse;
import org.springframework.stereotype.Component;

@Component
public class DomainMapper {

    private final EncryptionService encryptionService;

    public DomainMapper(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    public Payment toDomain(PaymentCommand command, Webhook webhook) {
        return Payment.builder()
                .webhookId(webhook.getId())
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .zipCode(command.getZipCode())
                .encryptedCardNumber(encryptionService.encrypt(command.getCardNumber()))
                .build();
    }

    public PaymentResponse fromDomain(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .firstName(payment.getFirstName())
                .lastName(payment.getLastName())
                .zipCode(payment.getZipCode())
                .maskedCardNumber(encryptionService.maskCard(payment.getEncryptedCardNumber()))
                .build();
    }

    public Payment toDomain(PaymentEntity entity) {
        return Payment.builder()
                .id(entity.getId())
                .webhookId(entity.getWebhookId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .zipCode(entity.getZipCode())
                .encryptedCardNumber(entity.getEncryptedCardNumber())
                .build();
    }

    public PaymentEntity toEntity(Payment payment) {
        PaymentEntity.PaymentEntityBuilder builder = PaymentEntity.builder()
                .webhookId(payment.getWebhookId())
                .firstName(payment.getFirstName())
                .lastName(payment.getLastName())
                .zipCode(payment.getZipCode())
                .encryptedCardNumber(payment.getEncryptedCardNumber());
        
        if (payment.getId() != null) {
            builder.id(payment.getId());
        }
        
        return builder.build();
    }

    public Webhook toDomain(WebhookCommand command) {
        return Webhook.builder()
                .url(command.getUrl())
                .description(command.getDescription())
                .secret(command.getSecret())
                .isActive(true)
                .build();
    }


    public WebhookEntity toEntity(Webhook webhook) {
        WebhookEntity.WebhookEntityBuilder builder = WebhookEntity.builder()
                .url(webhook.getUrl())
                .description(webhook.getDescription())
                .secret(webhook.getSecret())
                .isActive(webhook.getIsActive())
                .createdAt(webhook.getCreatedAt())
                .updatedAt(webhook.getUpdatedAt());
        
        if (webhook.getId() != null) {
            builder.id(webhook.getId());
        }
        
        return builder.build();
    }

    public Webhook toDomain(WebhookEntity entity) {
        return Webhook.builder()
                .id(entity.getId())
                .url(entity.getUrl())
                .description(entity.getDescription())
                .secret(entity.getSecret())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
