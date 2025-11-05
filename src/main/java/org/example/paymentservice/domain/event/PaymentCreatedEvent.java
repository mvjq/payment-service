package org.example.paymentservice.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.paymentservice.domain.model.Payment;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreatedEvent implements DomainEvent, Serializable {

    private UUID eventId;
    private UUID paymentId;
    private UUID webhookId;
    private LocalDateTime occurredAt;
    private PaymentEventData eventData;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentEventData implements Serializable {
        private String firstName;
        private String lastName;
        private String zipCode;
        private String maskedCardNumber;
    }

    @Override
    public String getAggregateType() {
        return "PAYMENT";
    }

    @Override
    public String getEventType() {
        return "PAYMENT_CREATED";
    }

    @Override
    public Object getEventData() {
        return this.eventData;
    }

    public static PaymentCreatedEvent create(Payment payment) {
        return PaymentCreatedEvent.builder()
                .eventId(UUID.randomUUID())
                .paymentId(payment.getId())
                .webhookId(payment.getWebhookId())
                .occurredAt(LocalDateTime.now())
                .eventData(PaymentEventData.builder()
                        .firstName(payment.getFirstName())
                        .lastName(payment.getLastName())
                        .zipCode(payment.getZipCode())
                        .maskedCardNumber(payment.getEncryptedCardNumber())
                        .build())
                .build();
    }
}