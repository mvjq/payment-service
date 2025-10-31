package org.example.paymentservice.domain.event;

import org.example.paymentservice.domain.model.Payment;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PaymentCreatedEventTest {

    public static final String NAME = "Marcos";
    public static final String LAST_NAME = "Doe";
    public static final String ZIP = "12345";
    public static final String ENCRYPTED = "encrypted";

    @Test
    void shouldCreateEventFromPayment() {
        Payment payment = Payment.builder()
                .id(UUID.randomUUID())
                .webhookId(UUID.randomUUID())
                .firstName(NAME)
                .lastName(LAST_NAME)
                .zipCode(ZIP)
                .encryptedCardNumber(ENCRYPTED)
                .build();

        PaymentCreatedEvent event = PaymentCreatedEvent.create(payment);

        assertNotNull(event);
        assertNotNull(event.getEventId());
        assertEquals(payment.getId(), event.getPaymentId());
        assertEquals(payment.getWebhookId(), event.getWebhookId());
        assertNotNull(event.getOccurredAt());
        assertEquals("PAYMENT", event.getAggregateType());
        assertEquals("PAYMENT_CREATED", event.getEventType());
    }

    @Test
    void shouldIncludeEventDataInEvent() {
        Payment payment = Payment.builder()
                .id(UUID.randomUUID())
                .webhookId(UUID.randomUUID())
                .firstName(NAME)
                .lastName(LAST_NAME)
                .zipCode(ZIP)
                .encryptedCardNumber(ENCRYPTED)
                .build();

        PaymentCreatedEvent event = PaymentCreatedEvent.create(payment);

        assertNotNull(event.getEventData());
        assertTrue(event.getEventData() instanceof PaymentCreatedEvent.PaymentEventData);
    }
}
