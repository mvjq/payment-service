package org.example.paymentservice.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public static PaymentCreatedEvent create(
            UUID paymentId,
            String firstName,
            String lastName,
            String zipCode,
            String maskedCardNumber
    ) {
        return PaymentCreatedEvent.builder()
                .eventId(UUID.randomUUID())
                .paymentId(paymentId)
                .occurredAt(LocalDateTime.now())
                .eventData(PaymentEventData.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .zipCode(zipCode)
                        .maskedCardNumber(maskedCardNumber)
                        .build())
                .build();
    }
}