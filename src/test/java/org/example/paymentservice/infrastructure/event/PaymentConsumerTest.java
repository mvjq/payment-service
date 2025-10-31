package org.example.paymentservice.infrastructure.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.paymentservice.domain.event.PaymentCreatedEvent;
import org.example.paymentservice.domain.model.Webhook;
import org.example.paymentservice.domain.port.out.WebhookClientPort;
import org.example.paymentservice.domain.port.out.WebhookRepositoryPort;
import org.example.paymentservice.infrastructure.adapter.external.WebhookClientResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentConsumerTest {

    public static final String HTTP_EXAMPLE_COM_WEBHOOK = "http://example.com/webhook";
    public static final String SECRET = "secret123";
    public static final String PAYMENT_DATA = "{\"payment\":\"data\"}";
    @Mock
    private WebhookRepositoryPort webhookRepositoryPort;

    @Mock
    private WebhookClientPort webhookClientPort;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PaymentConsumer paymentConsumer;

    private PaymentCreatedEvent event;
    private Webhook webhook;
    private UUID webhookId;

    @BeforeEach
    void setUp() {
        webhookId = UUID.randomUUID();
        
        event = PaymentCreatedEvent.builder()
                .eventId(UUID.randomUUID())
                .paymentId(UUID.randomUUID())
                .webhookId(webhookId)
                .occurredAt(LocalDateTime.now())
                .build();

        webhook = Webhook.builder()
                .id(webhookId)
                .url(HTTP_EXAMPLE_COM_WEBHOOK)
                .secret(SECRET)
                .isActive(true)
                .build();
    }

    @Test
    void shouldConsumeEventAndSendWebhook() throws Exception {
        when(webhookRepositoryPort.findById(webhookId)).thenReturn(Optional.of(webhook));
        when(objectMapper.writeValueAsString(event)).thenReturn(PAYMENT_DATA);
        when(webhookClientPort.sendWebhook(anyString(), anyString(), anyString()))
                .thenReturn(WebhookClientResponse.builder().success(true).statusCode(200).build());

        assertDoesNotThrow(() -> paymentConsumer.consumePaymentCreated(event));

        verify(webhookClientPort).sendWebhook(
                eq(HTTP_EXAMPLE_COM_WEBHOOK),
                anyString(),
                eq(SECRET)
        );
    }

    @Test
    void shouldNotSendWebhookWhenWebhookNotFound() throws Exception {
        when(webhookRepositoryPort.findById(webhookId)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> paymentConsumer.consumePaymentCreated(event));

        verify(webhookClientPort, never()).sendWebhook(anyString(), anyString(), anyString());
    }

    @Test
    void shouldNotSendWebhookWhenWebhookInactive() throws Exception {
        webhook.setIsActive(false);
        when(webhookRepositoryPort.findById(webhookId)).thenReturn(Optional.of(webhook));

        assertDoesNotThrow(() -> paymentConsumer.consumePaymentCreated(event));

        verify(webhookClientPort, never()).sendWebhook(anyString(), anyString(), anyString());
    }

    @Test
    void shouldThrowExceptionWhenWebhookFails() throws Exception {
        when(webhookRepositoryPort.findById(webhookId)).thenReturn(Optional.of(webhook));
        when(objectMapper.writeValueAsString(event)).thenReturn(PAYMENT_DATA);
        when(webhookClientPort.sendWebhook(anyString(), anyString(), anyString()))
                .thenReturn(WebhookClientResponse.builder()
                        .success(false)
                        .statusCode(500)
                        .errorMessage("Server error")
                        .build());

        assertThrows(RuntimeException.class, () -> 
            paymentConsumer.consumePaymentCreated(event)
        );
    }
}
