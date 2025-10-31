package org.example.paymentservice.domain;

import org.example.paymentservice.application.command.PaymentCommand;
import org.example.paymentservice.application.command.WebhookCommand;
import org.example.paymentservice.application.service.EncryptionService;
import org.example.paymentservice.domain.model.Payment;
import org.example.paymentservice.domain.model.Webhook;
import org.example.paymentservice.domain.model.WebhookHistory;
import org.example.paymentservice.domain.model.valueobject.WebhookStatus;
import org.example.paymentservice.infrastructure.adapter.persistence.entity.PaymentEntity;
import org.example.paymentservice.infrastructure.adapter.persistence.entity.WebhookEntity;
import org.example.paymentservice.infrastructure.adapter.persistence.entity.WebhookHistoryEntity;
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
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .zipCode(entity.getZipCode())
                .encryptedCardNumber(entity.getEncryptedCardNumber())
                .build();
    }

    public PaymentEntity toEntity(Payment payment) {
        return PaymentEntity.builder()
                .id(payment.getId())
                .firstName(payment.getFirstName())
                .lastName(payment.getLastName())
                .zipCode(payment.getZipCode())
                .encryptedCardNumber(payment.getEncryptedCardNumber())
                .build();
    }

    public Webhook toDomain(WebhookCommand command) {
        return Webhook.builder()
                .id(UUID.randomUUID())
                .url(command.getUrl())
                .description(command.getDescription())
                .secret(command.getSecret())
                .isActive(true)
                .build();
    }


    public WebhookEntity toEntity(Webhook webhook) {
        return WebhookEntity.builder()
                .id(webhook.getId())
                .url(webhook.getUrl())
                .description(webhook.getDescription())
                .secret(webhook.getSecret())
                .isActive(webhook.getIsActive())
                .createdAt(webhook.getCreatedAt())
                .updatedAt(webhook.getUpdatedAt())
                .build();
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

    public WebhookHistoryEntity toEntity(WebhookHistory webhookHistory) {
        return WebhookHistoryEntity.builder()
                .id(webhookHistory.getId())
                .webhookId(webhookHistory.getWebhookId())
                .paymentId(webhookHistory.getPaymentId())
                .eventId(webhookHistory.getEventId())
                .status(webhookHistory.getStatus().name())
                .requestUrl(webhookHistory.getRequestUrl())
                .requestPayload(webhookHistory.getRequestPayload())
                .build();
    }

    public WebhookHistory toDomain(WebhookHistoryEntity entity) {
        return WebhookHistory.builder()
                .id(entity.getId())
                .webhookId(entity.getWebhookId())
                .paymentId(entity.getPaymentId())
                .eventId(entity.getEventId())
                .status(WebhookStatus.valueOf(entity.getStatus()))
                .requestUrl(entity.getRequestUrl())
                .requestPayload(entity.getRequestPayload())
                .build();
    }
}
